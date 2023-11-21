package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.UserDataHandler;

import java.io.Closeable;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("菜品添加")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("菜品添加:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping ("/page")
    @ApiOperation("菜品分页")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("删除的菜品的id:{}",ids);
        dishService.deleteBatch(ids);
        //删除缓存
        String keys = "dish_*";
        deleteCache(keys);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取要修改的菜品信息")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("获取菜品id:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.update(dishDTO);
        //删除缓存
        String keys = "dish_*";
        deleteCache(keys);
        return Result.success();
    }

    //根据分类id查询菜品
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启售或停售菜品")
    public Result stopAndStart(@PathVariable("status") Integer status,Long id){
        dishService.changeStatus(status,id);
        //删除缓存
        String keys = "dish_*";
        deleteCache(keys);
        return Result.success();
    }

    private void deleteCache(String param){
        Set keys = redisTemplate.keys(param);
        redisTemplate.delete(keys);
    }


}
