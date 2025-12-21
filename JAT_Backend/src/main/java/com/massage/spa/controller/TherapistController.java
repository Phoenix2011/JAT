package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.Therapist;
import com.massage.spa.service.TherapistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 技师管理控制器
 */
@RestController
@RequestMapping("/api/therapist")
@Tag(name = "技师管理", description = "技师管理接口")
public class TherapistController {

    @Autowired
    private TherapistService therapistService;

    /**
     * 分页查询技师列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('therapist:list')")
    @Operation(summary = "分页查询技师列表", description = "分页查询技师列表")
    public Result<PageResult<Therapist>> list(
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "性别") @RequestParam(required = false) Integer gender,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Therapist> list = therapistService.listByPage(name, phone, gender, status, 
                pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = therapistService.count(name, phone, gender, status);
        PageResult<Therapist> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取技师详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('therapist:query')")
    @Operation(summary = "获取技师详情", description = "根据技师ID获取技师详情")
    public Result<Therapist> getInfo(@Parameter(description = "技师ID") @PathVariable Long id) {
        Therapist therapist = therapistService.getById(id);
        return Result.success(therapist);
    }

    /**
     * 添加技师
     */
    @PostMapping
    @PreAuthorize("hasAuthority('therapist:add')")
    @Operation(summary = "添加技师", description = "添加技师")
    public Result<Void> add(@Parameter(description = "技师信息") @Valid @RequestBody Therapist therapist) {
        boolean result = therapistService.add(therapist);
        return result ? Result.success() : Result.error("添加技师失败");
    }

    /**
     * 修改技师
     */
    @PutMapping
    @PreAuthorize("hasAuthority('therapist:edit')")
    @Operation(summary = "修改技师", description = "修改技师")
    public Result<Void> update(@Parameter(description = "技师信息") @Valid @RequestBody Therapist therapist) {
        boolean result = therapistService.update(therapist);
        return result ? Result.success() : Result.error("修改技师失败");
    }

    /**
     * 删除技师
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('therapist:remove')")
    @Operation(summary = "删除技师", description = "根据技师ID删除技师")
    public Result<Void> delete(@Parameter(description = "技师ID") @PathVariable Long id) {
        boolean result = therapistService.delete(id);
        return result ? Result.success() : Result.error("删除技师失败");
    }

    /**
     * 批量删除技师
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('therapist:remove')")
    @Operation(summary = "批量删除技师", description = "批量删除技师")
    public Result<Void> batchDelete(@Parameter(description = "技师ID列表") @RequestBody List<Long> ids) {
        boolean result = therapistService.batchDelete(ids);
        return result ? Result.success() : Result.error("批量删除技师失败");
    }

    /**
     * 修改技师状态
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('therapist:edit')")
    @Operation(summary = "修改技师状态", description = "修改技师状态")
    public Result<Void> updateStatus(
            @Parameter(description = "技师ID") @RequestParam Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        boolean result = therapistService.updateStatus(id, status);
        return result ? Result.success() : Result.error("修改技师状态失败");
    }

    /**
     * 获取技师服务统计
     */
    @GetMapping("/stats/{id}")
    @PreAuthorize("hasAuthority('therapist:query')")
    @Operation(summary = "获取技师服务统计", description = "获取技师服务统计")
    public Result<Object> getServiceStats(@Parameter(description = "技师ID") @PathVariable Long id) {
        Object stats = therapistService.getServiceStats(id);
        return Result.success(stats);
    }

    /**
     * 获取技师服务记录
     */
    @GetMapping("/service-records/{id}")
    @PreAuthorize("hasAuthority('therapist:query')")
    @Operation(summary = "获取技师服务记录", description = "获取技师服务记录")
    public Result<List<Object>> getServiceRecords(
            @Parameter(description = "技师ID") @PathVariable Long id,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Object> records = therapistService.getServiceRecords(id, pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(records);
    }

    /**
     * 获取技师提成记录
     */
    @GetMapping("/commission-records/{id}")
    @PreAuthorize("hasAuthority('therapist:query')")
    @Operation(summary = "获取技师提成记录", description = "获取技师提成记录")
    public Result<List<Object>> getCommissionRecords(
            @Parameter(description = "技师ID") @PathVariable Long id,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Object> records = therapistService.getCommissionRecords(id, pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(records);
    }
}
