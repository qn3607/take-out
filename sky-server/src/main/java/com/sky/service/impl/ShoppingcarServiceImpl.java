package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCarMapper;
import com.sky.service.ShoppingcarService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingcarServiceImpl implements ShoppingcarService {

    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealMapper setMealMapper;

    //往购物车中添加物品
    @Override
    public void insert(ShoppingCartDTO shoppingCartDTO) {
        //首先要看购物车里是否已存在菜品或套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        //如果存在,将数量+1
        if(list != null && list.size() > 0){
            ShoppingCart sc = list.get(0); //应该是通过在菜品页面或者套餐页面点击+来选购商品，所以list中只有对应的一个商品信息
            sc.setNumber(sc.getNumber() + 1);
            shoppingCarMapper.updateByNum(sc);
        }else{
            //如果不存在，判断是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            if(dishId != null){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setDishId(dishId);
                shoppingCart.setAmount(dish.getPrice());
            }else{
                Long setmealId = shoppingCart.getSetmealId();
                Setmeal sm = setMealMapper.getById(setmealId);
                shoppingCart.setName(sm.getName());
                shoppingCart.setImage(sm.getImage());
                shoppingCart.setSetmealId(setmealId);
                shoppingCart.setAmount(sm.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                        .userId(userId).build();
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        return list;
    }

    @Override
    public void cleanByUserId(Long userId) {
        shoppingCarMapper.deleteByUserId(userId);
    }

    @Override
    public void reduceOne(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查看购物车中的商品信息
        List<ShoppingCart> list = shoppingCarMapper.list(shoppingCart);
        if(list != null && list.size() > 0){
            ShoppingCart sc = list.get(0);
            Integer number = sc.getNumber();
            if(number == 1){
                shoppingCarMapper.deleteById(sc.getId());
            }else{
                sc.setNumber(sc.getNumber() - 1);
                shoppingCarMapper.updateByNum(sc);
            }
        }

    }
}
