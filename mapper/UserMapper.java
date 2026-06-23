package org.example.animalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.animalsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}