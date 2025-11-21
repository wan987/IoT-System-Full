package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("al_gateway")
public class Gateway {
    @TableId(type = IdType.AUTO)
    private Long gatewayId;

    private Long stationId;      // 关联的站点ID
    private String gatewayName;
    private String gatewayLoc;
    private String gatewayIp;    // 注意大小写：数据库是 gateway_IP，MyBatisPlus转驼峰为 gatewayIp
    private Integer gatewayPort;
    private String gatewayDtu;   // 数据库 gateway_DTU -> gatewayDtu
    private String status;
    private String delFlag;
    private LocalDateTime updateTime;
    private String remark;
}