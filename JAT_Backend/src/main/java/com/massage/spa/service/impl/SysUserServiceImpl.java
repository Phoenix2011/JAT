package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.SysRole;
import com.massage.spa.entity.SysRolePermission;
import com.massage.spa.entity.SysUser;
import com.massage.spa.mapper.SysPermissionMapper;
import com.massage.spa.mapper.SysRoleMapper;
import com.massage.spa.mapper.SysRolePermissionMapper;
import com.massage.spa.mapper.SysUserMapper;
import com.massage.spa.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {

        return sysPermissionMapper.selectPermissionKeysByUserId(userId);

        //// 获取角色权限
        //LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        //wrapper.eq(SysRolePermission::getRoleId, sysUser.getRoleId());
        //List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(wrapper);
        //
        //// 获取权限标识
        //List<String> permissions = new ArrayList<>();
        //for (SysRolePermission rolePermission : rolePermissions) {
        //    String permissionKey = sysPermissionMapper.selectPermissionKeyById(rolePermission.getPermissionId());
        //    if (StringUtils.isNotBlank(permissionKey)) {
        //        permissions.add(permissionKey);
        //    }
        //}
        //
        //return permissions;
    }

    @Override
    public boolean updateLoginTime(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setLastLoginTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        return sysUserMapper.updateById(sysUser) > 0;
    }

    @Override
    public List<SysUser> listByPage(String username, String realName, String phone, Integer status, Long current, Long size) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                .like(StringUtils.isNotBlank(realName), SysUser::getRealName, realName)
                .like(StringUtils.isNotBlank(phone), SysUser::getPhone, phone)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> resultPage = sysUserMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public Long count(String username, String realName, String phone, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username)
                .like(StringUtils.isNotBlank(realName), SysUser::getRealName, realName)
                .like(StringUtils.isNotBlank(phone), SysUser::getPhone, phone)
                .eq(status != null, SysUser::getStatus, status);
        return sysUserMapper.selectCount(wrapper);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysUser sysUser) {
        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, sysUser.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查角色是否存在
        SysRole sysRole = sysRoleMapper.selectById(sysUser.getRoleId());
        if (sysRole == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 加密密码
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        
        // 设置默认值
        sysUser.setStatus(1);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUser.setDeleted(0);
        
        return sysUserMapper.insert(sysUser) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser sysUser) {
        // 检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(sysUser.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户名是否重复
        if (!existUser.getUsername().equals(sysUser.getUsername())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, sysUser.getUsername());
            if (sysUserMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("用户名已存在");
            }
        }
        
        // 检查角色是否存在
        SysRole sysRole = sysRoleMapper.selectById(sysUser.getRoleId());
        if (sysRole == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 不更新密码
        sysUser.setPassword(null);
        
        // 设置更新时间
        sysUser.setUpdateTime(LocalDateTime.now());
        
        return sysUserMapper.updateById(sysUser) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        return sysUserMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        return sysUserMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        // 检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, existUser.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        
        // 更新密码
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setPassword(passwordEncoder.encode(newPassword));
        sysUser.setUpdateTime(LocalDateTime.now());
        
        return sysUserMapper.updateById(sysUser) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id) {
        // 检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置密码为123456
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setPassword(passwordEncoder.encode("123456"));
        sysUser.setUpdateTime(LocalDateTime.now());
        
        return sysUserMapper.updateById(sysUser) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查用户是否存在
        SysUser existUser = sysUserMapper.selectById(id);
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新状态
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setStatus(status);
        sysUser.setUpdateTime(LocalDateTime.now());
        
        return sysUserMapper.updateById(sysUser) > 0;
    }
}
