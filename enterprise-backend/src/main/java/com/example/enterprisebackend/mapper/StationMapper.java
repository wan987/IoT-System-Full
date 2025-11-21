package com.example.enterprisebackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprisebackend.entity.Station;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StationMapper extends BaseMapper<Station> {
    // 这里里面什么都不用写，MyBatis-Plus 已经帮你把活干完了
}