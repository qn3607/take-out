package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags="员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登入")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    //新增员工
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工...");
        employeeService.save(employeeDTO);
        return Result.success();
    }

    //员工信息分页
    @GetMapping("/page")
    @ApiOperation("分页查询员工信息")
    public Result<PageResult> getPage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页信息...");
        PageResult page = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(page);
    }

    //启用/禁用员工
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用员工")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用/禁用员工...");
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    //根据id查询员工信息
    @GetMapping("{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("员工的id为:{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    //编辑更新员工信息
    @PutMapping()
    @ApiOperation("编辑更新员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工的信息为：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

}
