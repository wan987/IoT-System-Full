package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok注解：自动生成getter, setter, toString等
@TableName("al_station") // 告诉MyBatis-Plus这个类对应数据库的哪张表
public class Station {

    @TableId(type = IdType.AUTO) // 对应数据库的自增主键 (IDENTITY)
    private Long stationId;

    private Long deptId;
    private String stationName;
    private String stationLoc;
    private String sLongitude; // 数据库是 s_longitude，这里自动转驼峰
    private String sLatitude;
    private String sAltitude;
    private String status;
    private String delFlag;
    private String createBy;
    private LocalDateTime createTime; // 对应 datetime 类型
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
}