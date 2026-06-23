package org.example.animalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.animalsystem.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
