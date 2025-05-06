package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查套餐
     * @param dishIds 菜品id
    */

    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

}
