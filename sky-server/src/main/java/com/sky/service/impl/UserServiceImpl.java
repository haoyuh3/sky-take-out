package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties wechatproperties;

    @Autowired
    private UserMapper userMapper;

    public final static String GRANT_CODE = "authorization_code";
    public final static String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     *
     * @param userLoginDTO 用户登录信息
     * @return User
     */
    public User wxlogin(UserLoginDTO userLoginDTO) {

        String code = userLoginDTO.getCode();
        String openid = getOpenid(code);
        // 如果openid为空，登录失败
        if (openid == null || openid.isEmpty()) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 如果openid不为空，登录成功
        User user = userMapper.getByOpenid(openid);

        if (user == null){
            user = User.builder()
                        .openid(openid)
                        .createTime(LocalDateTime.now())
                        .build();
            userMapper.insert(user);
        }

        return user;
    }

    /**
     * 获取openid
     *
     * @param code 微信登录code
     * @return openid
     */
    private String getOpenid(String code) {
        // 调用微信接口获取用户信息
        Map<String, String> param = new HashMap<>();
        param.put("appid", wechatproperties.getAppid());
        param.put("secret", wechatproperties.getSecret());
        param.put("js_code", code);
        param.put("grant_type", GRANT_CODE);
        String json = HttpClientUtil.doGet(WX_LOGIN, param);
        JSONObject jsonObject = JSON.parseObject(json);
        // 获取openid
        return jsonObject.getString("openid");
    }
}
