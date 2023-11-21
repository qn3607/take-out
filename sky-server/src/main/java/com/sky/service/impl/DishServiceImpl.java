package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    //用于新增菜品
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //插入一种菜品
        dishMapper.insert(dish);
        long id = dish.getId();
        //获取insert菜品生成的id
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){//判断用户是否有选择口味
            flavors.forEach(dishFlavor -> {//遍历每个口味，并将根据获得的id值进行设置
                dishFlavor.setDishId(id);
            });
            //插入多种口味
            dishFlavorMapper.insert(flavors);
        }


    }

    //用于菜品分页
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //利用PageHelper进行分页
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //用于根据ids删除菜品
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断删除的菜品是否在售
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) //表示当前菜品正在出售
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //判断删除的菜品是否和套餐关联
        List<Long> setMealId = setMealDishMapper.getSetMealId(ids);
        if(setMealId != null && setMealId.size() > 0)
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        //每个id进行一次sql删除太过于麻烦
       /*for(Long id:ids){
            //删除菜品
            dishMapper.deleteById(id);
            //删除菜品关联的口味
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //进行批量删除
        dishMapper.deleteByIds(ids);

        dishFlavorMapper.deleteByDishIds(ids);

    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品信息
        dishMapper.update(dish);
        //删除菜品口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新添加修改后的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){//判断用户是否有选择口味
            flavors.forEach(dishFlavor -> {//遍历每个口味，并将根据获得的id值进行设置
                dishFlavor.setDishId(dishDTO.getId());
            });
            //插入多种口味
            dishFlavorMapper.insert(flavors);
        }
    }

    //根据分类id查询菜品
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    //启售或停售菜品
    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
