package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.Order;
import com.massage.spa.entity.OrderDetail;
import com.massage.spa.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('order:list')")
    @Operation(summary = "分页查询订单列表", description = "分页查询订单列表")
    public Result<PageResult<Map<String, Object>>> list(
            @Parameter(description = "订单编号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "顾客姓名") @RequestParam(required = false) String customerName,
            @Parameter(description = "顾客手机号") @RequestParam(required = false) String customerPhone,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> list = orderService.listByPage(orderNo, customerName, customerPhone, 
                status, pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = orderService.count(orderNo, null, customerName, customerPhone, status);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    // 根据顾客ID获取订单列表
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "根据顾客ID获取订单列表", description = "根据顾客ID获取订单列表")
    public Result<PageResult<Map<String, Object>>> getOrdersByCustomerId(
            @Parameter(description = "顾客ID") @PathVariable Long customerId,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> list = orderService.getOrdersByCustomerId(customerId, pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = orderService.count(null, customerId, null, null, null);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详情")
    public Result<Order> getInfo(@Parameter(description = "订单ID") @PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }
    
    /**
     * 获取订单明细
     */
    @GetMapping("/details/{orderId}")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "获取订单明细", description = "根据订单ID获取订单明细")
    public Result<List<OrderDetail>> getOrderDetails(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        List<OrderDetail> details = orderService.getOrderDetails(orderId);
        return Result.success(details);
    }

    /**
     * 创建订单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('order:add')")
    @Operation(summary = "创建订单", description = "创建订单")
    public Result<Void> create(
            @Parameter(description = "订单信息") @Valid @RequestBody Order order,
            @Parameter(description = "订单明细") @RequestBody List<OrderDetail> orderDetails) {
        boolean result = orderService.create(order, orderDetails);
        return result ? Result.success() : Result.error("创建订单失败");
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay/{id}")
    @PreAuthorize("hasAuthority('order:edit')")
    @Operation(summary = "支付订单", description = "支付订单")
    public Result<Void> pay(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式") @RequestParam Integer paymentMethod,
            @Parameter(description = "支付金额") @RequestParam Double paymentAmount) {
        boolean result = orderService.pay(id, paymentMethod, paymentAmount);
        return result ? Result.success() : Result.error("支付订单失败");
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{id}")
    @PreAuthorize("hasAuthority('order:edit')")
    @Operation(summary = "取消订单", description = "取消订单")
    public Result<Void> cancel(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "取消原因") @RequestParam String cancelReason) {
        boolean result = orderService.cancel(id, cancelReason);
        return result ? Result.success() : Result.error("取消订单失败");
    }
    
    /**
     * 退款订单
     */
    @PutMapping("/refund/{id}")
    @PreAuthorize("hasAuthority('order:edit')")
    @Operation(summary = "退款订单", description = "退款订单")
    public Result<Void> refund(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "退款原因") @RequestParam String refundReason,
            @Parameter(description = "退款金额") @RequestParam Double refundAmount) {
        boolean result = orderService.refund(id, refundReason, refundAmount);
        return result ? Result.success() : Result.error("退款订单失败");
    }


    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "获取订单统计", description = "获取订单统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = orderService.getStats();
        return Result.success(stats);
    }
    
    /**
     * 获取今日订单统计
     */
    @GetMapping("/today-stats")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "获取今日订单统计", description = "获取今日订单统计")
    public Result<Map<String, Object>> getTodayStats() {
        Map<String, Object> stats = orderService.getTodayStats();
        return Result.success(stats);
    }
    
    /**
     * 获取本月订单统计
     */
    @GetMapping("/month-stats")
    @PreAuthorize("hasAuthority('order:query')")
    @Operation(summary = "获取本月订单统计", description = "获取本月订单统计")
    public Result<Map<String, Object>> getMonthStats() {
        Map<String, Object> stats = orderService.getMonthStats();
        return Result.success(stats);
    }
}
