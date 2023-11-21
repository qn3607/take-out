package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SetMealServiceImpl implements SetMealService {


    @Autowired
    private SetMealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //向数据库插入套餐信息
        setmealMapper.insert(setmeal);
        //获取套餐id
        Long id = setmeal.getId();
        //设置每个菜品关联的套餐id
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(id);
        });
        //保存套餐和菜品的关联关系
        setMealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            //启售的套餐不能删除
            if (StatusConstant.DISABLE == setmeal.getStatus())
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            //删除套餐表中的数据
            setmealMapper.deleteById(id);
            //删除套餐菜品关系表中的数据
            setMealDishMapper.deleteById(id);
        });
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        //如果要启售套餐，首先需要保证该套餐内是没有停售菜品
        if (status == StatusConstant.ENABLE) {
            List<Dish> list = dishMapper.getBySetmealId(id);
            if (list != null && list.size() > 0) {
                list.forEach(dish -> {
                    if (dish.getStatus() == StatusConstant.DISABLE)
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                });
            }

        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        setmealMapper.update(setmeal);
    }

    @Override
    public SetmealVO getByIdwithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> list = setMealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //修改套餐表
        setmealMapper.update(setmeal);
        //删除原有套餐中的菜品信息
        Long id = setmealDTO.getId();
        setMealDishMapper.getBySetmealId(id);
        //获取新的菜品信息
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //更新该菜品对应的套餐id
        setmealDishes.forEach(dish ->{
            dish.setSetmealId(id);
        });
        setMealDishMapper.insertBatch(setmealDishes);

    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
