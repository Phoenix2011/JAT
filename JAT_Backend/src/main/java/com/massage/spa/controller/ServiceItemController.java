package com.massage.spa.controller;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.common.page.PageResult;
import com.massage.spa.common.result.Result;
import com.massage.spa.entity.ServiceCategory;
import com.massage.spa.entity.ServiceItem;
import com.massage.spa.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 服务项目管理控制器
 */
@RestController
@RequestMapping("/api/service")
@Tag(name = "服务项目管理", description = "服务项目管理接口")
public class ServiceItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    /**
     * 分页查询服务项目列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('service:list')")
    @Operation(summary = "分页查询服务项目列表", description = "分页查询服务项目列表")
    public Result<PageResult<ServiceItem>> list(
            @Parameter(description = "名称") @RequestParam(required = false) String name,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        List<ServiceItem> list = serviceItemService.listByPage(name, categoryId, status, 
                pageQuery.getPageNum(), pageQuery.getPageSize());
        Long total = serviceItemService.count(name, categoryId, status);
        PageResult<ServiceItem> pageResult = new PageResult<>(total, pageQuery.getPageSize(), pageQuery.getPageNum(), list);
        return Result.success(pageResult);
    }

    /**
     * 获取服务项目详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('service:query')")
    @Operation(summary = "获取服务项目详情", description = "根据服务项目ID获取服务项目详情")
    public Result<ServiceItem> getInfo(@Parameter(description = "服务项目ID") @PathVariable Long id) {
        ServiceItem serviceItem = serviceItemService.getById(id);
        return Result.success(serviceItem);
    }

    /**
     * 添加服务项目
     */
    @PostMapping
    @PreAuthorize("hasAuthority('service:add')")
    @Operation(summary = "添加服务项目", description = "添加服务项目")
    public Result<Void> add(@Parameter(description = "服务项目信息") @Valid @RequestBody ServiceItem serviceItem) {
        boolean result = serviceItemService.add(serviceItem);
        return result ? Result.success() : Result.error("添加服务项目失败");
    }

    /**
     * 修改服务项目
     */
    @PutMapping
    @PreAuthorize("hasAuthority('service:edit')")
    @Operation(summary = "修改服务项目", description = "修改服务项目")
    public Result<Void> update(@Parameter(description = "服务项目信息") @Valid @RequestBody ServiceItem serviceItem) {
        boolean result = serviceItemService.update(serviceItem);
        return result ? Result.success() : Result.error("修改服务项目失败");
    }

    /**
     * 删除服务项目
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('service:remove')")
    @Operation(summary = "删除服务项目", description = "根据服务项目ID删除服务项目")
    public Result<Void> delete(@Parameter(description = "服务项目ID") @PathVariable Long id) {
        boolean result = serviceItemService.delete(id);
        return result ? Result.success() : Result.error("删除服务项目失败");
    }

    /**
     * 批量删除服务项目
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('service:remove')")
    @Operation(summary = "批量删除服务项目", description = "批量删除服务项目")
    public Result<Void> batchDelete(@Parameter(description = "服务项目ID列表") @RequestBody List<Long> ids) {
        boolean result = serviceItemService.batchDelete(ids);
        return result ? Result.success() : Result.error("批量删除服务项目失败");
    }

    /**
     * 修改服务项目状态
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('service:edit')")
    @Operation(summary = "修改服务项目状态", description = "修改服务项目状态")
    public Result<Void> updateStatus(
            @Parameter(description = "服务项目ID") @RequestParam Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        boolean result = serviceItemService.updateStatus(id, status);
        return result ? Result.success() : Result.error("修改服务项目状态失败");
    }

    /**
     * 获取所有服务分类
     */
    @GetMapping("/category/list")
    @PreAuthorize("hasAuthority('service:list')")
    @Operation(summary = "获取所有服务分类", description = "获取所有服务分类")
    public Result<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceItemService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 添加服务分类
     */
    @PostMapping("/category")
    @PreAuthorize("hasAuthority('service:add')")
    @Operation(summary = "添加服务分类", description = "添加服务分类")
    public Result<Void> addCategory(@Parameter(description = "服务分类信息") @Valid @RequestBody ServiceCategory serviceCategory) {
        boolean result = serviceItemService.addCategory(serviceCategory);
        return result ? Result.success() : Result.error("添加服务分类失败");
    }

    /**
     * 修改服务分类
     */
    @PutMapping("/category")
    @PreAuthorize("hasAuthority('service:edit')")
    @Operation(summary = "修改服务分类", description = "修改服务分类")
    public Result<Void> updateCategory(@Parameter(description = "服务分类信息") @Valid @RequestBody ServiceCategory serviceCategory) {
        boolean result = serviceItemService.updateCategory(serviceCategory);
        return result ? Result.success() : Result.error("修改服务分类失败");
    }

    /**
     * 删除服务分类
     */
    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasAuthority('service:remove')")
    @Operation(summary = "删除服务分类", description = "根据服务分类ID删除服务分类")
    public Result<Void> deleteCategory(@Parameter(description = "服务分类ID") @PathVariable Long id) {
        boolean result = serviceItemService.deleteCategory(id);
        return result ? Result.success() : Result.error("删除服务分类失败");
    }
}
