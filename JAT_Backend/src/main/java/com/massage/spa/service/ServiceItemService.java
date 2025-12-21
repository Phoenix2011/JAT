package com.massage.spa.service;

import com.massage.spa.entity.ServiceCategory;
import com.massage.spa.entity.ServiceItem;

import java.util.List;

/**
 * 服务项目服务接口
 */
public interface ServiceItemService {

    /**
     * 分页查询服务项目列表
     * @param name 名称
     * @param categoryId 分类ID
     * @param status 状态
     * @param current 当前页
     * @param size 每页记录数
     * @return 服务项目列表
     */
    List<ServiceItem> listByPage(String name, Long categoryId, Integer status, Long current, Long size);

    /**
     * 获取服务项目总数
     * @param name 名称
     * @param categoryId 分类ID
     * @param status 状态
     * @return 服务项目总数
     */
    Long count(String name, Long categoryId, Integer status);

    /**
     * 根据ID获取服务项目
     * @param id 服务项目ID
     * @return 服务项目对象
     */
    ServiceItem getById(Long id);

    /**
     * 添加服务项目
     * @param serviceItem 服务项目对象
     * @return 是否成功
     */
    boolean add(ServiceItem serviceItem);

    /**
     * 更新服务项目
     * @param serviceItem 服务项目对象
     * @return 是否成功
     */
    boolean update(ServiceItem serviceItem);

    /**
     * 删除服务项目
     * @param id 服务项目ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 批量删除服务项目
     * @param ids 服务项目ID列表
     * @return 是否成功
     */
    boolean batchDelete(List<Long> ids);
    
    /**
     * 修改服务项目状态
     * @param id 服务项目ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 获取所有服务分类
     * @return 服务分类列表
     */
    List<ServiceCategory> getAllCategories();
    
    /**
     * 添加服务分类
     * @param serviceCategory 服务分类对象
     * @return 是否成功
     */
    boolean addCategory(ServiceCategory serviceCategory);
    
    /**
     * 更新服务分类
     * @param serviceCategory 服务分类对象
     * @return 是否成功
     */
    boolean updateCategory(ServiceCategory serviceCategory);
    
    /**
     * 删除服务分类
     * @param id 服务分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);
}
