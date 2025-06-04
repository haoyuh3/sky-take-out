package com.sky.service.impl;

import com.sky.document.DishDocument;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.repository.DishSearchRepository;
import com.sky.service.DishSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
* 菜品搜索服务实现类
*/
@Service
@Slf4j
public class DishSearchServiceImpl implements DishSearchService {

   @Autowired
   private DishSearchRepository dishSearchRepository;

   @Autowired
   private DishMapper dishMapper;

   @Autowired
   private CategoryMapper categoryMapper;

   @Autowired
   private ElasticsearchRestTemplate elasticsearchRestTemplate;

   @Autowired
   @Qualifier("dishRedisTemplate")
   private RedisTemplate<Object, Object> redisTemplate;

   private static final String HOT_SEARCH_KEY = "hot_search_keywords";
   private static final String SEARCH_HISTORY_KEY = "search_history:";

   @Override
   public void syncDishData() {
       try {
           log.info("开始同步菜品数据到Elasticsearch");

           // 清空现有索引
           dishSearchRepository.deleteAll();

           // 获取所有菜品数据
           List<Dish> dishes = dishMapper.list();

           List<DishDocument> dishDocuments = new ArrayList<>();
           for (Dish dish : dishes) {
               DishDocument dishDocument = new DishDocument();
               BeanUtils.copyProperties(dish, dishDocument);

               // 获取分类名称
               Category category = categoryMapper.getById(dish.getCategoryId());
               if (category != null) {
                   dishDocument.setCategoryName(category.getName());
               }

               // 设置默认值
               dishDocument.setSalesCount(0);
               dishDocument.setRating(5.0);
               dishDocument.setTags(new String[]{"美食", "推荐"});

               dishDocuments.add(dishDocument);
           }

           // 批量保存到Elasticsearch
           dishSearchRepository.saveAll(dishDocuments);

           log.info("菜品数据同步完成，共同步{}条数据", dishDocuments.size());

       } catch (Exception e) {
           log.error("同步菜品数据到Elasticsearch失败", e);
       }
   }

   @Override
   public void indexDish(DishDocument dishDocument) {
       try {
           dishSearchRepository.save(dishDocument);
           log.info("菜品索引更新成功：{}", dishDocument.getName());
       } catch (Exception e) {
           log.error("菜品索引更新失败：{}", dishDocument.getName(), e);
       }
   }

   @Override
   public void deleteDish(Long dishId) {
       try {
           dishSearchRepository.deleteById(dishId);
           log.info("菜品索引删除成功：{}", dishId);
       } catch (Exception e) {
           log.error("菜品索引删除失败：{}", dishId, e);
       }
   }

   @Override
   public Page<DishDocument> searchDishes(String keyword, Integer page, Integer size) {
       try {
           // 记录搜索关键词
           recordSearchKeyword(keyword);

           Pageable pageable = PageRequest.of(page - 1, size,
               Sort.by(Sort.Direction.DESC, "salesCount", "rating"));

           if (StringUtils.hasText(keyword)) {
               return dishSearchRepository.findByNameContaining(keyword, pageable);
           } else {
               return dishSearchRepository.findByStatus(1, pageable); // 只返回启售的菜品
           }

       } catch (Exception e) {
           log.error("搜索菜品失败：keyword={}", keyword, e);
           return Page.empty();
       }
   }

   @Override
   public Page<DishDocument> advancedSearchDishes(DishPageQueryDTO queryDTO) {
       try {
           Pageable pageable = PageRequest.of(queryDTO.getPage() - 1, queryDTO.getPageSize(),
               Sort.by(Sort.Direction.DESC, "salesCount", "rating"));

           String name = queryDTO.getName();
           Long categoryId = Long.valueOf(queryDTO.getCategoryId());
           Integer status = queryDTO.getStatus();

           if (StringUtils.hasText(name) && categoryId != null) {
               return dishSearchRepository.findByNameContainingAndCategoryId(name, categoryId, pageable);
           } else if (StringUtils.hasText(name)) {
               return dishSearchRepository.findByNameContaining(name, pageable);
           } else if (categoryId != null) {
               return dishSearchRepository.findByCategoryId(categoryId, pageable);
           } else if (status != null) {
               return dishSearchRepository.findByStatus(status, pageable);
           } else {
               return dishSearchRepository.findAll(pageable);
           }

       } catch (Exception e) {
           log.error("高级搜索菜品失败：{}", queryDTO, e);
           return Page.empty();
       }
   }

   @Override
   public Page<DishDocument> searchDishesByCategory(Long categoryId, Integer page, Integer size) {
       try {
           Pageable pageable = PageRequest.of(page - 1, size,
               Sort.by(Sort.Direction.DESC, "salesCount", "rating"));

           return dishSearchRepository.findByCategoryId(categoryId, pageable);

       } catch (Exception e) {
           log.error("根据分类搜索菜品失败：categoryId={}", categoryId, e);
           return Page.empty();
       }
   }

   @Override
   public List<String> getHotSearchKeywords() {
       try {
           Set<Object> keywords = redisTemplate.opsForZSet().reverseRange(HOT_SEARCH_KEY, 0, 9);
           return keywords != null ? keywords.stream().map(Object::toString).collect(Collectors.toList()) : new ArrayList<>();
       } catch (Exception e) {
           log.error("获取热门搜索关键词失败", e);
           return new ArrayList<>();
       }
   }

   @Override
   public List<String> getSearchSuggestions(String keyword) {
       try {
           if (!StringUtils.hasText(keyword)) {
               return new ArrayList<>();
           }

           // 使用Elasticsearch的前缀查询获取建议
           NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
               .withQuery(prefixQuery("name", keyword))
               .withPageable(PageRequest.of(0, 10))
               .build();

           SearchHits<DishDocument> searchHits = elasticsearchRestTemplate.search(searchQuery, DishDocument.class);

           return searchHits.getSearchHits().stream()
               .map(SearchHit::getContent)
               .map(DishDocument::getName)
               .distinct()
               .collect(Collectors.toList());

       } catch (Exception e) {
           log.error("获取搜索建议失败：keyword={}", keyword, e);
           return new ArrayList<>();
       }
   }

   /**
    * 记录搜索关键词
    */
   private void recordSearchKeyword(String keyword) {
       if (StringUtils.hasText(keyword)) {
           try {
               // 增加热门搜索关键词的分数
               redisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, keyword, 1);
           } catch (Exception e) {
               log.error("记录搜索关键词失败：{}", keyword, e);
           }
       }
   }
}