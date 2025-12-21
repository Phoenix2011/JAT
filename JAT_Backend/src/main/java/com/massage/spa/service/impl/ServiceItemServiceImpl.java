package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.ServiceCategory;
import com.massage.spa.entity.ServiceItem;
import com.massage.spa.mapper.ServiceCategoryMapper;
import com.massage.spa.mapper.ServiceItemMapper;
import com.massage.spa.service.ServiceItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务项目服务实现类
 */
@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;
    
    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Override
    public List<ServiceItem> listByPage(String name, Long categoryId, Integer status, Long current, Long size) {
        Page<ServiceItem> page = new Page<>(current, size);
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), ServiceItem::getName, name)
                .eq(categoryId != null, ServiceItem::getCategoryId, categoryId)
                .eq(status != null, ServiceItem::getStatus, status)
                .orderByAsc(ServiceItem::getSort);
        Page<ServiceItem> resultPage = serviceItemMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public Long count(String name, Long categoryId, Integer status) {
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), ServiceItem::getName, name)
                .eq(categoryId != null, ServiceItem::getCategoryId, categoryId)
                .eq(status != null, ServiceItem::getStatus, status);
        return serviceItemMapper.selectCount(wrapper);
    }

    @Override
    public ServiceItem getById(Long id) {
        return serviceItemMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(ServiceItem serviceItem) {
        // 检查分类是否存在
        ServiceCategory category = serviceCategoryMapper.selectById(serviceItem.getCategoryId());
        if (category == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 设置默认值
        serviceItem.setStatus(1);
        serviceItem.setCreateTime(LocalDateTime.now());
        serviceItem.setUpdateTime(LocalDateTime.now());
        serviceItem.setDeleted(0);
        
        return serviceItemMapper.insert(serviceItem) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ServiceItem serviceItem) {
        // 检查服务项目是否存在
        ServiceItem existItem = serviceItemMapper.selectById(serviceItem.getId());
        if (existItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 检查分类是否存在
        ServiceCategory category = serviceCategoryMapper.selectById(serviceItem.getCategoryId());
        if (category == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 设置更新时间
        serviceItem.setUpdateTime(LocalDateTime.now());
        
        return serviceItemMapper.updateById(serviceItem) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查服务项目是否存在
        ServiceItem existItem = serviceItemMapper.selectById(id);
        if (existItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        return serviceItemMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        return serviceItemMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查服务项目是否存在
        ServiceItem existItem = serviceItemMapper.selectById(id);
        if (existItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 更新状态
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setId(id);
        serviceItem.setStatus(status);
        serviceItem.setUpdateTime(LocalDateTime.now());
        
        return serviceItemMapper.updateById(serviceItem) > 0;
    }

    @Override
    public List<ServiceCategory> getAllCategories() {
        LambdaQueryWrapper<ServiceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ServiceCategory::getSort);
        return serviceCategoryMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCategory(ServiceCategory serviceCategory) {
        // 设置默认值
        serviceCategory.setStatus(1);
        serviceCategory.setCreateTime(LocalDateTime.now());
        serviceCategory.setUpdateTime(LocalDateTime.now());
        serviceCategory.setDeleted(0);
        
        return serviceCategoryMapper.insert(serviceCategory) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(ServiceCategory serviceCategory) {
        // 检查服务分类是否存在
        ServiceCategory existCategory = serviceCategoryMapper.selectById(serviceCategory.getId());
        if (existCategory == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 设置更新时间
        serviceCategory.setUpdateTime(LocalDateTime.now());
        
        return serviceCategoryMapper.updateById(serviceCategory) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        // 检查服务分类是否存在
        ServiceCategory existCategory = serviceCategoryMapper.selectById(id);
        if (existCategory == null) {
            throw new BusinessException("服务分类不存在");
        }
        
        // 检查分类下是否有服务项目
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceItem::getCategoryId, id);
        if (serviceItemMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该分类下存在服务项目，无法删除");
        }
        
        return serviceCategoryMapper.deleteById(id) > 0;
    }
}
