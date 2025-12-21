package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.entity.ServiceRecord;
import com.massage.spa.entity.Therapist;
import com.massage.spa.mapper.ServiceRecordMapper;
import com.massage.spa.mapper.TherapistMapper;
import com.massage.spa.service.TherapistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技师服务实现类
 */
@Service
public class TherapistServiceImpl implements TherapistService {

    @Autowired
    private TherapistMapper therapistMapper;
    
    @Autowired
    private ServiceRecordMapper serviceRecordMapper;

    @Override
    public List<Therapist> listByPage(String name, String phone, Integer gender, Integer status, Long current, Long size) {
        Page<Therapist> page = new Page<>(current, size);
        LambdaQueryWrapper<Therapist> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Therapist::getName, name)
                .like(StringUtils.isNotBlank(phone), Therapist::getPhone, phone)
                .eq(gender != null, Therapist::getGender, gender)
                .eq(status != null, Therapist::getStatus, status)
                .orderByDesc(Therapist::getCreateTime);
        Page<Therapist> resultPage = therapistMapper.selectPage(page, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public Long count(String name, String phone, Integer gender, Integer status) {
        LambdaQueryWrapper<Therapist> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Therapist::getName, name)
                .like(StringUtils.isNotBlank(phone), Therapist::getPhone, phone)
                .eq(gender != null, Therapist::getGender, gender)
                .eq(status != null, Therapist::getStatus, status);
        return therapistMapper.selectCount(wrapper);
    }

    @Override
    public Therapist getById(Long id) {
        return therapistMapper.selectById(id);
    }

    @Override
    public Therapist getByPhone(String phone) {
        LambdaQueryWrapper<Therapist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Therapist::getPhone, phone);
        return therapistMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Therapist therapist) {
        // 检查手机号是否存在
        Therapist existTherapist = getByPhone(therapist.getPhone());
        if (existTherapist != null) {
            throw new BusinessException("手机号已存在");
        }
        
        // 设置默认值
        therapist.setStatus(1);
        therapist.setCreateTime(LocalDateTime.now());
        therapist.setUpdateTime(LocalDateTime.now());
        therapist.setDeleted(0);
        
        return therapistMapper.insert(therapist) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Therapist therapist) {
        // 检查技师是否存在
        Therapist existTherapist = therapistMapper.selectById(therapist.getId());
        if (existTherapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 检查手机号是否重复
        if (!existTherapist.getPhone().equals(therapist.getPhone())) {
            Therapist phoneTherapist = getByPhone(therapist.getPhone());
            if (phoneTherapist != null) {
                throw new BusinessException("手机号已存在");
            }
        }
        
        // 设置更新时间
        therapist.setUpdateTime(LocalDateTime.now());
        
        return therapistMapper.updateById(therapist) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 检查技师是否存在
        Therapist existTherapist = therapistMapper.selectById(id);
        if (existTherapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        return therapistMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        return therapistMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查技师是否存在
        Therapist existTherapist = therapistMapper.selectById(id);
        if (existTherapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 更新状态
        Therapist therapist = new Therapist();
        therapist.setId(id);
        therapist.setStatus(status);
        therapist.setUpdateTime(LocalDateTime.now());
        
        return therapistMapper.updateById(therapist) > 0;
    }

    @Override
    public Object getServiceStats(Long id) {
        // 检查技师是否存在
        Therapist therapist = therapistMapper.selectById(id);
        if (therapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 查询服务统计
        Map<String, Object> stats = new HashMap<>();
        
        // 服务次数
        Integer serviceCount = serviceRecordMapper.selectCountByTherapistId(id);
        stats.put("serviceCount", serviceCount != null ? serviceCount : 0);
        
        // 服务总时长（分钟）
        Integer totalDuration = serviceRecordMapper.selectTotalDurationByTherapistId(id);
        stats.put("totalDuration", totalDuration != null ? totalDuration : 0);
        
        // 总提成金额
        Double totalCommission = serviceRecordMapper.selectTotalCommissionByTherapistId(id);
        stats.put("totalCommission", totalCommission != null ? totalCommission : 0);
        
        // 最近服务时间
        LocalDateTime lastServiceTime = serviceRecordMapper.selectLastServiceTimeByTherapistId(id);
        stats.put("lastServiceTime", lastServiceTime);
        
        return stats;
    }

    @Override
    public List<Object> getServiceRecords(Long id, Long current, Long size) {
        // 检查技师是否存在
        Therapist therapist = therapistMapper.selectById(id);
        if (therapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 查询服务记录
        return Collections.singletonList(serviceRecordMapper.selectServiceDetailsByTherapistId(id, (current - 1) * size, size));
    }

    @Override
    public List<Object> getCommissionRecords(Long id, Long current, Long size) {
        // 检查技师是否存在
        Therapist therapist = therapistMapper.selectById(id);
        if (therapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 查询提成记录
        return Collections.singletonList(serviceRecordMapper.selectCommissionDetailsByTherapistId(id, (current - 1) * size, size));
    }
}
