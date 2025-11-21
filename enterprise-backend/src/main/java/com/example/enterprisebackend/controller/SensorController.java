package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.Sensor;
import com.example.enterprisebackend.mapper.SensorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @Autowired
    private SensorMapper sensorMapper;

    // 分页查询
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String name) {
        Page<Sensor> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Sensor> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("sensor_name", name);
        }
        queryWrapper.orderByDesc("sensor_id");
        return Result.success(sensorMapper.selectPage(page, queryWrapper));
    }

    // 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody Sensor sensor) {
        sensorMapper.insert(sensor);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/edit")
    public Result<?> update(@RequestBody Sensor sensor) {
        sensorMapper.updateById(sensor);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        sensorMapper.deleteById(id);
        return Result.success(null);
    }
}