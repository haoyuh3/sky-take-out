package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId 分类id
     * @return 菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish 菜品
     */
    @AutoFill(value= OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 菜品列表
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 删除菜品
     * @param id 菜品
     */
//    @Delete("delete from dish where id = #{id}")
//    void deleteById(Long id);

    /**
     * 批量删除菜品
     * @param ids 菜品id集合
     */
    void deleteBatchId(List<Long> ids);

    /**
     * 批量修改菜品状态
     * @param dish 菜品id集合
     */
    @AutoFill(value= OperationType.UPDATE)
    void updateById(Dish dish);
}
