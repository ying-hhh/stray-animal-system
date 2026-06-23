package org.example.animalsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.animalsystem.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    @Select("SELECT COUNT(*) FROM like_record WHERE check_in_id = #{checkInId}")
    int countByCheckInId(Long checkInId);
}