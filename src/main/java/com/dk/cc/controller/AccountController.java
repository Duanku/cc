package com.dk.cc.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dk.cc.common.dto.LoginDto;
import com.dk.cc.common.lang.Result;
import com.dk.cc.common.util.JwtUtils;
import com.dk.cc.common.util.RedisUtil;
import com.dk.cc.entity.User;
import com.dk.cc.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private RedisUtil redisUtil;

    //过期时间
    @Value("${dk.jwt.refreshTokenExpireTime}")
    private long refreshTokenExpireTime;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response) {
        System.out.println("开始登录");
        if (loginDto == null){
            return Result.fail("请输入账号或密码");
        }
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        Assert.notNull(user, "用户不存在");

        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("密码不正确");
        }
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("存入时间"+currentTimeMillis);
        String jwt = jwtUtils.generateToken(user.getId(),currentTimeMillis);
        redisUtil.set(user.getId().toString(),currentTimeMillis,refreshTokenExpireTime);
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email", user.getEmail())
                .map()
        );
    }

    @GetMapping("/logout")
    @RequiresAuthentication
    public Result logout(HttpServletRequest request, HttpServerResponse response) {
        String token=request.getHeader("Authorization");
        redisUtil.del(jwtUtils.getUserId(token));
        return Result.succ(null);
    }

}
