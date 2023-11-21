package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "微信用户端")
public class UserController {

    @Autowired
    public UserService userService;

    @Autowired
    public JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<UserLoginVO> logIn(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信登入,{}",userLoginDTO.getCode());
        User user = userService.wxLogin(userLoginDTO);
        //生成jwt令牌
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getAdminTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }
}
