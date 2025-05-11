package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺接口")
@Slf4j
public class ShopController {
    /**
     * redisTemplate
     */
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    public final static String SHOP_STATUS = "SHOP_STATUS";
    /**
     * 获取店铺状态
     * @return Result
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        if (status == null) {
            return Result.success(0);
        }
        log.info("获取status:{}", status == 1 ? "营业中" : "打烊");
        return Result.success(status);
    }
}
