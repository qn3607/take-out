package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingcarService {
    void insert(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> list();

    void cleanByUserId(Long userId);

    void reduceOne(ShoppingCartDTO shoppingCartDTO);
}
