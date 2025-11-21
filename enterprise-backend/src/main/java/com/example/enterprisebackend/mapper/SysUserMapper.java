package com.example.enterprisebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprisebackend.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}