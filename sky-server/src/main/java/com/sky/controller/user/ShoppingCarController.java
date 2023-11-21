package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingcarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车相关状态接口")
public class ShoppingCarController {

    @Autowired
    private ShoppingcarService shoppingcarService;

    @ApiOperation("添加购物车")
    @PostMapping ("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingcarService.insert(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> check(){
        List<ShoppingCart> list = shoppingcarService.list();
        return Result.success(list);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result clean(){
        Long userId = BaseContext.getCurrentId();
        shoppingcarService.cleanByUserId(userId);
        return Result.success();
    }

    @ApiOperation("减少购物车中的商品数量")
    @PostMapping("/sub")
    public Result reduce(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingcarService.reduceOne(shoppingCartDTO);
        return Result.success();
    }

}
