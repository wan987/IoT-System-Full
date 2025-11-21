package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("al_maintenance_log")
public class MaintenanceLog {
    @TableId(type = IdType.AUTO)
    private Long logId;

    private LocalDateTime logTime;
    private String location;
    private String handler;      // 处理人
    private String eventDesc;    // 事件描述
    private String delFlag;
    private LocalDateTime createTime;
    private String remark;
}