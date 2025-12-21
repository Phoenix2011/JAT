package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.Customer;
import com.massage.spa.service.CustomerService;
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
 * 顾客管理控制器
 */
@RestController
@RequestMapping("/api/customer")
@Tag(name = "顾客管理", description = "顾客管理接口")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 分页查询顾客列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('customer:list')")
    @Operation(summary = "分页查询顾客列表", description = "分页查询顾客列表")
    public Result<PageResult<Customer>> list(
            @Parameter(description = "ID") @RequestParam(required = false) Long id,
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Customer> list = customerService.listByPage(id, name, phone,
                pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = customerService.count(id, name, phone);
        PageResult<Customer> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取顾客详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:query')")
    @Operation(summary = "获取顾客详情", description = "根据顾客ID获取顾客详情")
    public Result<Map<String, Object>> getExtInfo(@Parameter(description = "顾客ID") @PathVariable Long id) {
        Map<String, Object> customerInfo = customerService.getExtInfoById(id);
        return Result.success(customerInfo);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('customer:query')")
    @Operation(summary = "顾客查询", description = "根据顾客ID或姓名查询顾客")
    public Result<List<Customer>> search(
            @Parameter(description = "顾客ID或姓名") @RequestParam(required = false) String param,
            @Parameter(description = "顾客ID或姓名") @RequestParam(required = false) String text
    ) {
        List<Customer> customers = customerService.getByIdOrName(param);
        return Result.success(customers);
    }

    /**
     * 添加顾客
     */
    @PostMapping
    @PreAuthorize("hasAuthority('customer:add')")
    @Operation(summary = "添加顾客", description = "添加顾客")
    public Result<Void> add(@Parameter(description = "顾客信息") @Valid @RequestBody Customer customer) {
        boolean result = customerService.add(customer);
        return result ? Result.success() : Result.error("添加顾客失败");
    }

    /**
     * 修改顾客
     */
    @PutMapping
    @PreAuthorize("hasAuthority('customer:edit')")
    @Operation(summary = "修改顾客", description = "修改顾客")
    public Result<Void> update(@Parameter(description = "顾客信息") @Valid @RequestBody Customer customer) {
        boolean result = customerService.update(customer);
        return result ? Result.success() : Result.error("修改顾客失败");
    }

    /**
     * 删除顾客
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:remove')")
    @Operation(summary = "删除顾客", description = "根据顾客ID删除顾客")
    public Result<Void> delete(@Parameter(description = "顾客ID") @PathVariable Long id) {
        boolean result = customerService.delete(id);
        return result ? Result.success() : Result.error("删除顾客失败");
    }

    /**
     * 批量删除顾客
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('customer:remove')")
    @Operation(summary = "批量删除顾客", description = "批量删除顾客")
    public Result<Void> batchDelete(@Parameter(description = "顾客ID列表") @RequestBody List<Long> ids) {
        boolean result = customerService.batchDelete(ids);
        return result ? Result.success() : Result.error("批量删除顾客失败");
    }

    /**
     * 获取顾客消费统计
     */
    @GetMapping("/stats/{id}")
    @PreAuthorize("hasAuthority('customer:query')")
    @Operation(summary = "获取顾客消费统计", description = "获取顾客消费统计")
    public Result<Object> getConsumptionStats(@Parameter(description = "顾客ID") @PathVariable Long id) {
        Object stats = customerService.getConsumptionStats(id);
        return Result.success(stats);
    }

    /**
     * 获取顾客卡项列表
     */
    @GetMapping("/cards/{id}")
    @PreAuthorize("hasAuthority('customer:query')")
    @Operation(summary = "获取顾客卡项列表", description = "获取顾客卡项列表")
    public Result<List<Object>> getCustomerCards(@Parameter(description = "顾客ID") @PathVariable Long id) {
        List<Object> cards = customerService.getCustomerCards(id);
        return Result.success(cards);
    }

    /**
     * 获取顾客消费记录
     */
    @GetMapping("/consumption/{id}")
    @PreAuthorize("hasAuthority('customer:query')")
    @Operation(summary = "获取顾客消费记录", description = "获取顾客消费记录")
    public Result<PageResult<Map<String, Object>>> getConsumptionRecords(
            @Parameter(description = "顾客ID") @PathVariable Long id,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<Map<String, Object>> records = customerService.getConsumptionRecords(id, pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = 15L;
        PageResult<Map<String, Object>> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), records);
        return Result.success(pageResult);
    }
}
