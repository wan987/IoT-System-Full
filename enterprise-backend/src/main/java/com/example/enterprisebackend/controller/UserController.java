package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.SysUser;
import com.example.enterprisebackend.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserMapper userMapper;

    // 登录接口
    @PostMapping("/login")
    public Result<?> login(@RequestBody SysUser user) {
        // 1. 根据用户名查询
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        SysUser dbUser = userMapper.selectOne(query);

        // 2. 校验是否存在
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验密码 (这里为了简单直接比较明文，实际开发通常会加密)
        if (!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        // 登录成功，返回用户信息
        return Result.success(dbUser);
    }

    // 注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        // 检查用户名是否重复
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        if (userMapper.selectCount(query) > 0) {
            return Result.error("用户名已存在");
        }

        // 默认设置
        if (user.getRoleType() == null) {
            user.setRoleType(1); // 默认为数据管理员
        }

        userMapper.insert(user);
        return Result.success(user);
    }
}