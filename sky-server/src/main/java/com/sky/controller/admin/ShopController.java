package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
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
     * 设置店铺状态
     * @param status 店铺状态 1：营业中 0：打烊
     * @return Result
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result<String> setStatus(@PathVariable Integer status){
        log.info("设置status:{}", status == 1 ? "营业中" : "打烊");
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }

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
