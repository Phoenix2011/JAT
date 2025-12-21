package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.result.Result;
import com.massage.spa.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邀请返利管理控制器
 */
@RestController
@RequestMapping("/api/invitation")
@Tag(name = "邀请返利管理", description = "邀请返利管理接口")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    /**
     * 生成邀请码
     */
    @PostMapping("/generate-code/{customerId}")
    @PreAuthorize("hasAuthority('invitation:edit')")
    @Operation(summary = "生成邀请码", description = "为顾客生成随机邀请码")
    public Result<String> generateInvitationCode(@Parameter(description = "顾客ID") @PathVariable Long customerId) {
        String invitationCode = invitationService.generateInvitationCode(customerId);
        return Result.success(invitationCode);
    }

    /**
     * 自定义邀请码
     */
    @PutMapping("/customize-code/{customerId}")
    @PreAuthorize("hasAuthority('invitation:edit')")
    @Operation(summary = "自定义邀请码", description = "为顾客设置自定义邀请码")
    public Result<Void> customizeInvitationCode(
            @Parameter(description = "顾客ID") @PathVariable Long customerId,
            @Parameter(description = "自定义邀请码") @RequestParam String customCode) {
        boolean result = invitationService.customizeInvitationCode(customerId, customCode);
        return result ? Result.success() : Result.error("自定义邀请码失败");
    }

    /**
     * 创建邀请关系
     */
    @PostMapping("/relation")
    @PreAuthorize("hasAuthority('invitation:add')")
    @Operation(summary = "创建邀请关系", description = "创建邀请关系")
    public Result<Void> createInvitationRelation(
            @Parameter(description = "邀请人顾客ID") @RequestParam Long inviterCustomerId,
            @Parameter(description = "被邀请人顾客ID") @RequestParam Long inviteeCustomerId,
            @Parameter(description = "邀请码") @RequestParam String invitationCode) {
        boolean result = invitationService.createInvitationRelation(inviterCustomerId, inviteeCustomerId, invitationCode);
        return result ? Result.success() : Result.error("创建邀请关系失败");
    }

    /**
     * 创建邀请奖励
     */
    @PostMapping("/reward")
    @PreAuthorize("hasAuthority('invitation:add')")
    @Operation(summary = "创建邀请奖励", description = "创建邀请奖励")
    public Result<Void> createInvitationReward(
            @Parameter(description = "邀请关系ID") @RequestParam Long invitationRelationId,
            @Parameter(description = "订单ID") @RequestParam Long orderId,
            @Parameter(description = "订单金额") @RequestParam Double orderAmount,
            @Parameter(description = "佣金比例") @RequestParam Double commissionRate) {
        boolean result = invitationService.createInvitationReward(invitationRelationId, orderId, orderAmount, commissionRate);
        return result ? Result.success() : Result.error("创建邀请奖励失败");
    }

    /**
     * 解冻佣金
     */
    @PutMapping("/unfreeze")
    @PreAuthorize("hasAuthority('invitation:edit')")
    @Operation(summary = "解冻佣金", description = "解冻佣金")
    public Result<Void> unfreezeCommission(
            @Parameter(description = "订单ID") @RequestParam Long orderId,
            @Parameter(description = "解冻比例") @RequestParam Double unfreezeRatio) {
        boolean result = invitationService.unfreezeCommission(orderId, unfreezeRatio);
        return result ? Result.success() : Result.error("解冻佣金失败");
    }

    /**
     * 提现佣金
     */
    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('invitation:edit')")
    @Operation(summary = "提现佣金", description = "提现佣金")
    public Result<Void> withdrawCommission(
            @Parameter(description = "顾客ID") @RequestParam Long customerId,
            @Parameter(description = "提现金额") @RequestParam Double amount,
            @Parameter(description = "折扣比例") @RequestParam Double discountRatio) {
        boolean result = invitationService.withdrawCommission(customerId, amount, discountRatio);
        return result ? Result.success() : Result.error("提现佣金失败");
    }

    /**
     * 获取顾客邀请统计
     */
    @GetMapping("/stats/{customerId}")
    @PreAuthorize("hasAuthority('invitation:query')")
    @Operation(summary = "获取顾客邀请统计", description = "获取顾客邀请统计")
    public Result<Map<String, Object>> getCustomerInvitationStats(@Parameter(description = "顾客ID") @PathVariable Long customerId) {
        Map<String, Object> stats = invitationService.getCustomerInvitationStats(customerId);
        return Result.success(stats);
    }

    /**
     * 获取顾客邀请列表
     */
    @GetMapping("/list/{customerId}")
    @PreAuthorize("hasAuthority('invitation:query')")
    @Operation(summary = "获取顾客邀请列表", description = "获取顾客邀请列表")
    public Result<List<Map<String, Object>>> getCustomerInvitationList(@Parameter(description = "顾客ID") @PathVariable Long customerId) {
        List<Map<String, Object>> list = invitationService.getCustomerInvitationList(customerId);
        return Result.success(list);
    }

    /**
     * 获取顾客佣金记录
     */
    @GetMapping("/commission-records/{customerId}")
    @PreAuthorize("hasAuthority('invitation:query')")
    @Operation(summary = "获取顾客佣金记录", description = "获取顾客佣金记录")
    public Result<List<Map<String, Object>>> getCustomerCommissionRecords(
            @Parameter(description = "顾客ID") @PathVariable Long customerId,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> records = invitationService.getCustomerCommissionRecords(customerId, 
                pageQuery.getPageNum(), pageQuery.getPageSize());
        return Result.success(records);
    }
}
