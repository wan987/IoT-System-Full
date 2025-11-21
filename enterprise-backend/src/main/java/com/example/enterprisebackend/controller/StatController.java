package com.example.enterprisebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.enterprisebackend.common.Result;
import com.example.enterprisebackend.entity.SensorData;
import com.example.enterprisebackend.mapper.GatewayMapper;
import com.example.enterprisebackend.mapper.SensorDataMapper;
import com.example.enterprisebackend.mapper.SensorMapper;
import com.example.enterprisebackend.mapper.StationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private GatewayMapper gatewayMapper;
    @Autowired
    private SensorMapper sensorMapper;
    @Autowired
    private SensorDataMapper dataMapper;

    @GetMapping("/summary")
    public Result<?> getSummary() {
        Map<String, Long> map = new HashMap<>();

        // 1. 站点总数
        map.put("stationCount", stationMapper.selectCount(null));

        // 2. 网关总数
        map.put("gatewayCount", gatewayMapper.selectCount(null));

        // 3. 传感器总数
        map.put("sensorCount", sensorMapper.selectCount(null));

        // 4. 报警数据总数 (查询 数值 > 阈值 的记录)
        QueryWrapper<SensorData> query = new QueryWrapper<>();
        query.apply("value > alarm_threshold");
        map.put("alarmCount", dataMapper.selectCount(query));

        return Result.success(map);
    }
}