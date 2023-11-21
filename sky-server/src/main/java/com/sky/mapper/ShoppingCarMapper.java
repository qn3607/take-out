package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ShoppingCarMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where user_id = #{userId}")
    void updateByNum(ShoppingCart sc);

    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "VALUES (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})" )
    void insert(ShoppingCart shoppingCart);


    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);
}
