package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.SensorData;
import com.example.enterprisebackend.mapper.SensorDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
public class SensorDataController {

    @Autowired
    private SensorDataMapper dataMapper;

    // 分页查询 (这里的搜索暂时留空，通常是按时间范围查，为了简单先查所有)
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SensorData> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SensorData> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("data_id"); // 最新数据在前
        return Result.success(dataMapper.selectPage(page, queryWrapper));
    }

    // 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody SensorData data) {
        dataMapper.insert(data);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/edit")
    public Result<?> update(@RequestBody SensorData data) {
        dataMapper.updateById(data);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dataMapper.deleteById(id);
        return Result.success(null);
    }
}