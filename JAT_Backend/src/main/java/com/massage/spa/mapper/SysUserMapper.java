package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
