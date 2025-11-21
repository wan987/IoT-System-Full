package com.example.enterprisebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprisebackend.entity.SensorData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SensorDataMapper extends BaseMapper<SensorData> {
}