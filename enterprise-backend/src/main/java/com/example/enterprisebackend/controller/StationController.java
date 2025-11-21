package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.Station;
import com.example.enterprisebackend.mapper.StationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils; // 注意导包是 springframework 的
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/station")
public class StationController {

    @Autowired
    private StationMapper stationMapper;

    // 分页查询 (增加了权限控制逻辑)
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String name,
                              // 新增两个参数，从前端传过来
                              @RequestParam(required = false) Integer roleType,
                              @RequestParam(required = false) Long deptId) {

        Page<Station> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Station> queryWrapper = new QueryWrapper<>();

        // 1. 原有的搜索逻辑
        if (StringUtils.hasText(name)) {
            queryWrapper.like("station_name", name);
        }

        // 2. === 权限控制核心逻辑 ===
        // 如果是“数据管理员” (roleType == 1)，且部门ID不为空
        if (roleType != null && roleType == 1 && deptId != null) {
            queryWrapper.eq("dept_id", deptId); // 强制拼接 WHERE dept_id = ?
        }

        queryWrapper.orderByDesc("create_time");
        return Result.success(stationMapper.selectPage(page, queryWrapper));
    }

    // 2. 新增接口
    // POST http://localhost:8080/station/add
    @PostMapping("/add")
    public Result<?> add(@RequestBody Station station) {
        stationMapper.insert(station);
        return Result.success(null);
    }

    // 3. 修改接口
    // PUT http://localhost:8080/station/edit
    @PutMapping("/edit")
    public Result<?> update(@RequestBody Station station) {
        stationMapper.updateById(station);
        return Result.success(null);
    }

    // 4. 删除接口
    // DELETE http://localhost:8080/station/delete/1
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        stationMapper.deleteById(id);
        return Result.success(null);
    }

    // 5. 查询所有站点 (用于下拉框选择)
    // GET http://localhost:8080/station/list
    @GetMapping("/list")
    public Result<?> listAll() {
        return Result.success(stationMapper.selectList(null));
    }
}