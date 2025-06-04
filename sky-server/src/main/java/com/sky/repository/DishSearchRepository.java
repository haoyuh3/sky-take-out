package com.sky.repository;

import com.sky.document.DishDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
* 菜品搜索Repository
*/
@Repository
public interface DishSearchRepository extends ElasticsearchRepository<DishDocument, Long> {

   /**
    * 根据菜品名称搜索
    */
   Page<DishDocument> findByNameContaining(String name, Pageable pageable);

   /**
    * 根据分类ID搜索
    */
   Page<DishDocument> findByCategoryId(Long categoryId, Pageable pageable);

   /**
    * 根据价格范围搜索
    */
   Page<DishDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

   /**
    * 根据状态搜索
    */
   Page<DishDocument> findByStatus(Integer status, Pageable pageable);

   /**
    * 复合搜索：名称和分类
    */
   Page<DishDocument> findByNameContainingAndCategoryId(String name, Long categoryId, Pageable pageable);

   /**
    * 复合搜索：名称、分类和价格范围
    */
   Page<DishDocument> findByNameContainingAndCategoryIdAndPriceBetween(
           String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

   /**
    * 根据标签搜索
    */
   Page<DishDocument> findByTagsIn(List<String> tags, Pageable pageable);
}