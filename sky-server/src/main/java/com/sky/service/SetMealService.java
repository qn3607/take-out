package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    //新增套餐，同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void changeStatus(Integer status, Long id);

    SetmealVO getByIdwithDish(Long id);

    void update(SetmealDTO setmealDTO);

    //条件查询
    List<Setmeal> list(Setmeal setmeal);

    //根据id查询菜品选项
    List<DishItemVO> getDishItemById(Long id);
}
