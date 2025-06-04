package com.sky.controller.user;

import com.sky.document.DishDocument;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端菜品搜索控制器
 */
@RestController("userDishSearchController")
@RequestMapping("/user/dish/search")
@Api(tags = "用户端菜品搜索接口")
@Slf4j
public class DishSearchController {

    @Autowired
    private DishSearchService dishSearchService;

    /**
     * 搜索菜品
     */
    @GetMapping
    @ApiOperation("搜索菜品")
    public Result<PageResult> searchDishes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("搜索菜品：keyword={}, page={}, size={}", keyword, page, size);

        Page<DishDocument> pageResult = dishSearchService.searchDishes(keyword, page, size);

        PageResult result = new PageResult();
        result.setTotal(pageResult.getTotalElements());
        result.setRecords(pageResult.getContent());

        return Result.success(result);
    }

    /**
     * 根据分类搜索菜品
     */
    @GetMapping("/category/{categoryId}")
    @ApiOperation("根据分类搜索菜品")
    public Result<PageResult> searchDishesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("根据分类搜索菜品：categoryId={}, page={}, size={}", categoryId, page, size);

        Page<DishDocument> pageResult = dishSearchService.searchDishesByCategory(categoryId, page, size);

        PageResult result = new PageResult();
        result.setTotal(pageResult.getTotalElements());
        result.setRecords(pageResult.getContent());

        return Result.success(result);
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/hot-keywords")
    @ApiOperation("获取热门搜索关键词")
    public Result<List<String>> getHotSearchKeywords() {
        log.info("获取热门搜索关键词");

        List<String> keywords = dishSearchService.getHotSearchKeywords();

        return Result.success(keywords);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @ApiOperation("获取搜索建议")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        log.info("获取搜索建议：keyword={}", keyword);

        List<String> suggestions = dishSearchService.getSearchSuggestions(keyword);

        return Result.success(suggestions);
    }
}