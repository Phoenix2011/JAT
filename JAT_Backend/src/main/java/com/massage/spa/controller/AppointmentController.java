package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.Appointment;
import com.massage.spa.service.AppointmentService;
import com.massage.spa.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约管理控制器
 */
@RestController
@RequestMapping("/api/appointment")
@Tag(name = "预约管理", description = "预约管理接口")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询预约列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('appointment:list')")
    @Operation(summary = "分页查询预约列表", description = "分页查询预约列表")
    public Result<PageResult<Map<String, Object>>> list(
            @Parameter(description = "顾客ID") @RequestParam(required = false) String customerId,
            @Parameter(description = "顾客姓名") @RequestParam(required = false) String customerName,
            @Parameter(description = "技师姓名") @RequestParam(required = false) String therapistName,
            @Parameter(description = "服务项目ID") @RequestParam(required = false) Long serviceId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "预约日期开始") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "预约日期结束") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> list = appointmentService.listByPage(customerId, customerName, therapistName,
                serviceId, status, fromDate, toDate, pageQuery);
        Long total = appointmentService.count(customerId, customerName, therapistName,
                serviceId, status, fromDate, toDate);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        //sysUserService.resetPassword((long)1);
        return Result.success(pageResult);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "根据日期区间获取预约列表", description = "可选技师ID，返回指定日期区间的所有预约")
    public Result<List<Map<String, Object>>> getAppointmentsByDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @Parameter(description = "技师ID") @RequestParam(required = false) Long therapistId) {
        List<Map<String, Object>> appointments = appointmentService.getAppointmentsByDate(fromDate, toDate, therapistId);
        return Result.success(appointments);
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "获取预约详情", description = "根据预约ID获取预约详情")
    public Result<Map<String, Object>> getInfo(@Parameter(description = "预约ID") @PathVariable Long id) {
        Map<String, Object> appointmentExt = appointmentService.getAppointmentExtById(id);
        return Result.success(appointmentExt);
    }

    @GetMapping("customer/{id}")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "获取预约详情", description = "根据顾客ID获取预约详情")
    public Result<List<Map<String, Object>>> getInfoByCustomerID(@Parameter(description = "Customer ID") @PathVariable Long id) {
        List<Map<String, Object>> appointmentExt = appointmentService.getAppointmentExtByCustomerId(id);
        return Result.success(appointmentExt);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('appointment:edit')")
    @Operation(summary = "更新预约状态", description = "根据预约ID更新预约状态")
    public Result<Void> updateStatus(
            @Parameter(description = "预约ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        Appointment appointment = appointmentService.getById(id);
        if (appointment == null) {
            return Result.error("预约不存在");
        }
        appointment.setStatus(status);
        boolean result = appointmentService.update(appointment);
        return result ? Result.success() : Result.error("更新预约状态失败");
    }

    /**
     * 添加预约
     */
    @PostMapping
    @PreAuthorize("hasAuthority('appointment:add')")
    @Operation(summary = "添加预约", description = "添加预约")
    public Result<Void> add(@Parameter(description = "预约信息") @Valid @RequestBody Appointment appointment) {
        boolean result = appointmentService.add(appointment);
        return result ? Result.success() : Result.error("添加预约失败");
    }

    /**
     * 修改预约
     */
    @PutMapping
    @PreAuthorize("hasAuthority('appointment:edit')")
    @Operation(summary = "修改预约", description = "修改预约")
    public Result<Void> update(@Parameter(description = "预约信息") @Valid @RequestBody Appointment appointment) {
        boolean result = appointmentService.update(appointment);
        return result ? Result.success() : Result.error("修改预约失败");
    }

    /**
     * 取消预约
     */
    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasAuthority('appointment:edit')")
    @Operation(summary = "取消预约", description = "取消预约")
    public Result<Void> cancel(
            @Parameter(description = "预约ID") @PathVariable Long id,
            @Parameter(description = "取消原因") @RequestParam String cancelReason) {
        boolean result = appointmentService.cancel(id, cancelReason);
        return result ? Result.success() : Result.error("取消预约失败");
    }

    /**
     * 完成预约
     */
    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('appointment:edit')")
    @Operation(summary = "完成预约", description = "完成预约")
    public Result<Void> complete(@Parameter(description = "预约ID") @PathVariable Long id) {
        boolean result = appointmentService.complete(id);
        return result ? Result.success() : Result.error("完成预约失败");
    }

    /**
     * 获取技师某日的预约时间段
     */
    @GetMapping("/time-slots")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "获取技师某日的预约时间段", description = "获取技师某日的预约时间段")
    public Result<List<Map<String, Object>>> getTherapistTimeSlots(
            @Parameter(description = "技师ID") @RequestParam Long therapistId,
            @Parameter(description = "日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Map<String, Object>> timeSlots = appointmentService.getTherapistTimeSlots(therapistId, date);
        return Result.success(timeSlots);
    }

    /**
     * 获取今日预约统计
     */
    @GetMapping("/today-stats")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "获取今日预约统计", description = "获取今日预约统计")
    public Result<Map<String, Object>> getTodayStats() {
        Map<String, Object> stats = appointmentService.getTodayStats();
        return Result.success(stats);
    }

    /**
     * 获取未来一周预约统计
     */
    @GetMapping("/week-stats")
    @PreAuthorize("hasAuthority('appointment:query')")
    @Operation(summary = "获取未来一周预约统计", description = "获取未来一周预约统计")
    public Result<Map<String, Object>> getWeekStats() {
        Map<String, Object> stats = appointmentService.getWeekStats();
        return Result.success(stats);
    }
}
