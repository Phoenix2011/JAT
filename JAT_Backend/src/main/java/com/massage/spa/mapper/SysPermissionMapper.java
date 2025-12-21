package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    
//    已弃用，必须基于permissionid查询key
//    @Select("SELECT permission_key FROM sys_permission WHERE id = #{id} AND deleted = 0")
//    String selectPermissionKeyById(@Param("id") Integer id);

    @Select(
        "SELECT p.permission_key " +
        "FROM sys_user u " +
        "JOIN sys_role r ON u.role_id = r.id " +
        "JOIN sys_role_permission rp ON r.id = rp.role_id " +
        "JOIN sys_permission p ON rp.permission_id = p.id " +
        "WHERE u.id = #{userId} and p.permission_key is not null ")
    List<String> selectPermissionKeysByUserId(Long userId);
}
