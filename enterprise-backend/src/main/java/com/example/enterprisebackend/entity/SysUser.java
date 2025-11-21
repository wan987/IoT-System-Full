package com.example.enterprisebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;
    private String password;
    private Long deptId;
    private Integer roleType; // 0:超级管理员 1:数据管理员
    private LocalDateTime createTime;
}