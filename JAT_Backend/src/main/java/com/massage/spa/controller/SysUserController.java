package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.SysUser;
import com.massage.spa.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统用户控制器
 */
@RestController
@RequestMapping("/api/system/user")
@Tag(name = "系统用户管理", description = "系统用户管理接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    @Operation(summary = "分页查询用户列表", description = "分页查询用户列表")
    public Result<PageResult<SysUser>> list(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<SysUser> list = sysUserService.listByPage(username, realName, phone, status, 
                pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = sysUserService.count(username, realName, phone, status);
        PageResult<SysUser> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详情")
    public Result<SysUser> getInfo(@Parameter(description = "用户ID") @PathVariable Long id) {
        SysUser sysUser = sysUserService.getById(id);
        return Result.success(sysUser);
    }

    /**
     * 添加用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @Operation(summary = "添加用户", description = "添加用户")
    public Result<Void> add(@Parameter(description = "用户信息") @Valid @RequestBody SysUser sysUser) {
        boolean result = sysUserService.add(sysUser);
        return result ? Result.success() : Result.error("添加用户失败");
    }

    /**
     * 修改用户
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Operation(summary = "修改用户", description = "修改用户")
    public Result<Void> update(@Parameter(description = "用户信息") @Valid @RequestBody SysUser sysUser) {
        boolean result = sysUserService.update(sysUser);
        return result ? Result.success() : Result.error("修改用户失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result<Void> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean result = sysUserService.delete(id);
        return result ? Result.success() : Result.error("删除用户失败");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:remove')")
    @Operation(summary = "批量删除用户", description = "批量删除用户")
    public Result<Void> batchDelete(@Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        boolean result = sysUserService.batchDelete(ids);
        return result ? Result.success() : Result.error("批量删除用户失败");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
//    @PreAuthorize("hasAuthority('system:user:edit')")
    @Operation(summary = "修改密码", description = "修改密码")
    public Result<Void> updatePassword(
            @Parameter(description = "用户ID") @RequestParam Long id,
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        boolean result = sysUserService.updatePassword(id, oldPassword, newPassword);
        return result ? Result.success() : Result.error("修改密码失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Operation(summary = "重置密码", description = "重置密码为123456")
    public Result<Void> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean result = sysUserService.resetPassword(id);
        return result ? Result.success() : Result.error("重置密码失败");
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Operation(summary = "修改用户状态", description = "修改用户状态")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @RequestParam Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        boolean result = sysUserService.updateStatus(id, status);
        return result ? Result.success() : Result.error("修改用户状态失败");
    }
}
