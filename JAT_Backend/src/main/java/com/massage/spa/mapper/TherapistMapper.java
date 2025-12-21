package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.Therapist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 技师Mapper接口
 */
@Mapper
public interface TherapistMapper extends BaseMapper<Therapist> {
}
