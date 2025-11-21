package com.example.enterprisebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprisebackend.entity.Sensor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SensorMapper extends BaseMapper<Sensor> {
}