package org.example.animalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.animalsystem.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}