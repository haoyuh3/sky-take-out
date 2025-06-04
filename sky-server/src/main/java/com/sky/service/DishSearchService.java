package com.sky.service;

import com.sky.document.DishDocument;
import com.sky.dto.DishPageQueryDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 菜品搜索服务
 */
public interface DishSearchService {

    /**
     * 同步菜品数据到Elasticsearch
     */
    void syncDishData();

    /**
     * 添加或更新菜品到搜索索引
     */
    void indexDish(DishDocument dishDocument);

    /**
     * 从搜索索引中删除菜品
     */
    void deleteDish(Long dishId);

    /**
     * 搜索菜品
     */
    Page<DishDocument> searchDishes(String keyword, Integer page, Integer size);

    /**
     * 高级搜索菜品
     */
    Page<DishDocument> advancedSearchDishes(DishPageQueryDTO queryDTO);

    /**
     * 根据分类搜索菜品
     */
    Page<DishDocument> searchDishesByCategory(Long categoryId, Integer page, Integer size);

    /**
     * 获取热门搜索关键词
     */
    List<String> getHotSearchKeywords();

    /**
     * 搜索建议（自动补全）
     */
    List<String> getSearchSuggestions(String keyword);
}