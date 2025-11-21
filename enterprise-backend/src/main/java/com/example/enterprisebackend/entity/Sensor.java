package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("al_sensor")
public class Sensor {
    @TableId(type = IdType.AUTO)
    private Long sensorId;

    private Long stationId;      // 归属站点
    private Long gatewayId;      // 归属网关
    private String sensorName;
    private String sensorLoc;
    private String sensorDtu;    // 数据库 sensor_DTU
    private String sensorType;   // 气象_水质_土壤_气体
    private String status;
    private String delFlag;
    private LocalDateTime createTime;
    private String remark;
}