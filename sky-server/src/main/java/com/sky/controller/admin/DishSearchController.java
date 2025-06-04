package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.DishSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端菜品搜索控制器
 */
@RestController("adminDishSearchController")
@RequestMapping("/admin/dish/search")
@Api(tags = "管理端菜品搜索接口")
@Slf4j
public class DishSearchController {

    @Autowired
    private DishSearchService dishSearchService;

    /**
     * 同步菜品数据到Elasticsearch
     */
    @PostMapping("/sync")
    @ApiOperation("同步菜品数据到Elasticsearch")
    public Result<String> syncDishData() {
        log.info("开始同步菜品数据到Elasticsearch");

        dishSearchService.syncDishData();

        return Result.success("菜品数据同步成功");
    }

    /**
     * 删除菜品搜索索引
     */
    @DeleteMapping("/{dishId}")
    @ApiOperation("删除菜品搜索索引")
    public Result<String> deleteDishIndex(@PathVariable Long dishId) {
        log.info("删除菜品搜索索引：dishId={}", dishId);

        dishSearchService.deleteDish(dishId);

        return Result.success("菜品搜索索引删除成功");
    }
}