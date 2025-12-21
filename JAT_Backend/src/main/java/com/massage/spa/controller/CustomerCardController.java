package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.CustomerCard;
import com.massage.spa.service.CustomerCardService;
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
 * 顾客卡项管理控制器
 */
@RestController
@RequestMapping("/api/customer-card")
@Tag(name = "顾客卡项管理", description = "顾客卡项管理接口")
public class CustomerCardController {

    @Autowired
    private CustomerCardService customerCardService;

    /**
     * 分页查询顾客卡项列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('customer-card:list')")
    @Operation(summary = "分页查询顾客卡项列表", description = "分页查询顾客卡项列表")
    public Result<PageResult<Map<String, Object>>> list(
            @Parameter(description = "顾客姓名") @RequestParam(required = false) String customerName,
            @Parameter(description = "顾客手机号") @RequestParam(required = false) String customerPhone,
            @Parameter(description = "服务项目ID") @RequestParam(required = false) Long serviceId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> list = customerCardService.listByPage(customerName, customerPhone, 
                serviceId, status, pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = customerCardService.count(customerName, customerPhone, serviceId, status);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取顾客卡项详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer-card:query')")
    @Operation(summary = "获取顾客卡项详情", description = "根据顾客卡项ID获取顾客卡项详情")
    public Result<CustomerCard> getInfo(@Parameter(description = "顾客卡项ID") @PathVariable Long id) {
        CustomerCard customerCard = customerCardService.getById(id);
        return Result.success(customerCard);
    }

    /**
     * 添加顾客卡项
     */
    @PostMapping
    @PreAuthorize("hasAuthority('customer-card:add')")
    @Operation(summary = "添加顾客卡项", description = "添加顾客卡项")
    public Result<Void> add(@Parameter(description = "顾客卡项信息") @Valid @RequestBody CustomerCard customerCard) {
        boolean result = customerCardService.add(customerCard);
        return result ? Result.success() : Result.error("添加顾客卡项失败");
    }

    /**
     * 修改顾客卡项
     */
    @PutMapping
    @PreAuthorize("hasAuthority('customer-card:edit')")
    @Operation(summary = "修改顾客卡项", description = "修改顾客卡项")
    public Result<Void> update(@Parameter(description = "顾客卡项信息") @Valid @RequestBody CustomerCard customerCard) {
        boolean result = customerCardService.update(customerCard);
        return result ? Result.success() : Result.error("修改顾客卡项失败");
    }

    /**
     * 删除顾客卡项
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer-card:remove')")
    @Operation(summary = "删除顾客卡项", description = "根据顾客卡项ID删除顾客卡项")
    public Result<Void> delete(@Parameter(description = "顾客卡项ID") @PathVariable Long id) {
        boolean result = customerCardService.delete(id);
        return result ? Result.success() : Result.error("删除顾客卡项失败");
    }
    
    /**
     * 使用卡项
     */
    @PutMapping("/use/{id}")
    @PreAuthorize("hasAuthority('customer-card:edit')")
    @Operation(summary = "使用卡项", description = "使用卡项")
    public Result<Void> useCard(
            @Parameter(description = "顾客卡项ID") @PathVariable Long id,
            @Parameter(description = "使用次数") @RequestParam Integer usedCount,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        boolean result = customerCardService.useCard(id, usedCount, remark);
        return result ? Result.success() : Result.error("使用卡项失败");
    }
    
    /**
     * 获取卡项使用记录
     */
    @GetMapping("/usage-records/{cardId}")
    @PreAuthorize("hasAuthority('customer-card:query')")
    @Operation(summary = "获取卡项使用记录", description = "获取卡项使用记录")
    public Result<List<Map<String, Object>>> getCardUsageRecords(@Parameter(description = "卡项ID") @PathVariable Long cardId) {
        List<Map<String, Object>> records = customerCardService.getCardUsageRecords(cardId);
        return Result.success(records);
    }
    
    /**
     * 获取顾客所有卡项
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('customer-card:query')")
    @Operation(summary = "获取顾客所有卡项", description = "获取顾客所有卡项")
    public Result<List<Map<String, Object>>> getCustomerCards(@Parameter(description = "顾客ID") @PathVariable Long customerId) {
        List<Map<String, Object>> cards = customerCardService.getCustomerCards(customerId);
        return Result.success(cards);
    }
    
    /**
     * 获取卡项统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('customer-card:query')")
    @Operation(summary = "获取卡项统计", description = "获取卡项统计")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = customerCardService.getStats();
        return Result.success(stats);
    }
}
