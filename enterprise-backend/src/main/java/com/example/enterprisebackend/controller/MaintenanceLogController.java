package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.MaintenanceLog;
import com.example.enterprisebackend.mapper.MaintenanceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class MaintenanceLogController {

    @Autowired
    private MaintenanceLogMapper logMapper;

    // 分页查询 (支持按 处理人 模糊搜索)
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String handler) {
        Page<MaintenanceLog> page = new Page<>(pageNum, pageSize);
        QueryWrapper<MaintenanceLog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(handler)) {
            queryWrapper.like("handler", handler);
        }
        queryWrapper.orderByDesc("log_time"); // 按日志发生时间倒序
        return Result.success(logMapper.selectPage(page, queryWrapper));
    }

    // 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody MaintenanceLog log) {
        logMapper.insert(log);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/edit")
    public Result<?> update(@RequestBody MaintenanceLog log) {
        logMapper.updateById(log);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        logMapper.deleteById(id);
        return Result.success(null);
    }
}