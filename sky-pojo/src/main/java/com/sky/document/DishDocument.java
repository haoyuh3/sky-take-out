package com.sky.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* 菜品搜索文档
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "dish_index")
public class DishDocument {

   @Id
   private Long id;

   @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
   private String name;

   @Field(type = FieldType.Long)
   private Long categoryId;

   @Field(type = FieldType.Text)
   private String categoryName;

   @Field(type = FieldType.Double)
   private BigDecimal price;

   @Field(type = FieldType.Keyword)
   private String image;

   @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
   private String description;

   @Field(type = FieldType.Integer)
   private Integer status;

   @Field(type = FieldType.Long)
   private Long createUser;

   @Field(type = FieldType.Long)
   private Long updateUser;


   // 销量字段，用于排序
   @Field(type = FieldType.Integer)
   private Integer salesCount;

   // 评分字段，用于排序
   @Field(type = FieldType.Double)
   private Double rating;

   // 标签字段，用于分类搜索
   @Field(type = FieldType.Keyword)
   private String[] tags;
}