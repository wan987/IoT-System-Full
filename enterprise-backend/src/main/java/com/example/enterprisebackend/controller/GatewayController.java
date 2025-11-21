package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.Gateway;
import com.example.enterprisebackend.mapper.GatewayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @Autowired
    private GatewayMapper gatewayMapper;

    // 分页查询
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String name) {
        Page<Gateway> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Gateway> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like("gateway_name", name);
        }
        // 按ID倒序
        queryWrapper.orderByDesc("gateway_id");
        return Result.success(gatewayMapper.selectPage(page, queryWrapper));
    }

    // 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody Gateway gateway) {
        gatewayMapper.insert(gateway);
        return Result.success(null);
    }

    // 修改
    @PutMapping("/edit")
    public Result<?> update(@RequestBody Gateway gateway) {
        gatewayMapper.updateById(gateway);
        return Result.success(null);
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        gatewayMapper.deleteById(id);
        return Result.success(null);
    }
    // 补充在 GatewayController 类里
    // 根据站点ID查询网关列表 (用于级联选择)
    @GetMapping("/listByStation")
    public Result<?> listByStation(@RequestParam Long stationId) {
        QueryWrapper<Gateway> query = new QueryWrapper<>();
        query.eq("station_id", stationId); // WHERE station_id = ?
        return Result.success(gatewayMapper.selectList(query));
    }
}