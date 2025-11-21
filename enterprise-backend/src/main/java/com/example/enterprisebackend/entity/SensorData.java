package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("al_data")
public class SensorData {
    @TableId(type = IdType.AUTO)
    private Long dataId;

    private Long sensorId;
    private Long gatewayId;
    private LocalDate dataDate;   // 日期
    private String dataTime;      // 时间 (数据库设计是varchar)
    private BigDecimal value;     // 采集数值 (用BigDecimal保证精度)
    private BigDecimal alarmThreshold; // 报警阈值
    private String alarmSmsSent;  // 0否 1是
    private Long dataType;        // 数据类型
    private Long dataUnit;        // 数据单位
    private String delFlag;
    private LocalDateTime createTime;
    private String remark;
}