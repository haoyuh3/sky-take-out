package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO 菜品DTO
     */
    @Transactional //多个数据表操作需要事务
    public void saveWithFlavor(DishDTO dishDTO){
        //菜品表插入数据 1
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        //菜品口味表插入数据 n
        List<DishFlavor> flavors= dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //设置菜品id
            for (DishFlavor df : flavors) {
                df.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);

        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 菜品分页查询DTO
     * @return PageResult
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> list = page.getResult();

        return new PageResult(total, list);

    }
    /**
     * 根据id删除菜品
     * @param ids list of ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断存在否
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            dishFlavorMapper.deteleByDishId(id);
//        }

        dishMapper.deleteBatchId(ids);
        dishFlavorMapper.deleteBatchDishId(ids);

    }
}
