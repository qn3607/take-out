package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @ApiOperation("新增套餐")
    @PostMapping
    @CachePut(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setMealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐，{}", setmealPageQueryDTO);
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除套餐，{}", ids);
        setMealService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售或停售")
    @CacheEvict(cacheNames = "setmealCache", key = "#id")
    public Result stopAndStart(@PathVariable("status") Integer status, Long id) {
        log.info("套餐启售和停售");
        setMealService.changeStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐及关联的菜品信息")
    public Result<SetmealVO> selectById(@PathVariable Long id) {
        SetmealVO setmealVO = setMealService.getByIdwithDish(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setMealService.update(setmealDTO);
        return Result.success();
    }


}
