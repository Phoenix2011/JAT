package com.massage.spa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.massage.spa.common.exception.BusinessException;
import com.massage.spa.common.page.PageQuery;
import com.massage.spa.entity.Appointment;
import com.massage.spa.entity.Customer;
import com.massage.spa.entity.ServiceItem;
import com.massage.spa.entity.Therapist;
import com.massage.spa.mapper.AppointmentMapper;
import com.massage.spa.mapper.CustomerMapper;
import com.massage.spa.mapper.ServiceItemMapper;
import com.massage.spa.mapper.TherapistMapper;
import com.massage.spa.service.AppointmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.massage.spa.util.SqlTool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约服务实现类
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private TherapistMapper therapistMapper;
    
    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Override
    public List<Map<String, Object>> listByPage(String customerId, String customerName, String therapistName,
                                              Long serviceId, Integer status, LocalDate fromDate, LocalDate toDate,
                                                PageQuery pageQuery) {

        String orderClause = SqlTool.generateOrderClause(pageQuery.getOrderField(), pageQuery.getIsAsc());

        return appointmentMapper.selectAppointmentsByPage(customerId, customerName, therapistName,
                serviceId, status, fromDate, toDate, (pageQuery.getPageNum() - 1) * pageQuery.getPageSize(),
                pageQuery.getPageSize(), orderClause);
    }

    @Override
    public List<Map<String, Object>> getAppointmentsByDate(LocalDate fromDate, LocalDate toDate, Long therapistId) {
        return appointmentMapper.selectAppointmentsByDate(fromDate, toDate, therapistId);
    }


    @Override
    public Long count(String customerId, String customerName, String therapistName,
                     Long serviceId, Integer status, LocalDate fromDate, LocalDate toDate) {
        return appointmentMapper.countAppointments(customerId, customerName, therapistName,
                serviceId, status, fromDate, toDate);
    }

    @Override
    public Appointment getById(Long id) {
        return appointmentMapper.selectById(id);
    }

    public Map<String, Object> getAppointmentExtById(Long id) {
        return appointmentMapper.selectAppointmentExt(id, null);
    }

    public List<Map<String, Object>> getAppointmentExtByCustomerId(Long id) {
        return appointmentMapper.selectAppointmentExtList(null, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Appointment appointment) {
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(appointment.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查技师是否存在
        Therapist therapist = therapistMapper.selectById(appointment.getTherapistId());
        if (therapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 检查服务项目是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(appointment.getServiceId());
        if (serviceItem == null) {
            throw new BusinessException("服务项目不存在");
        }
        
        // 检查时间段是否可预约
        boolean available = checkTimeSlotAvailable(appointment.getTherapistId(), 
                appointment.getAppointmentDate(), appointment.getStartTime(), appointment.getEndTime());
        if (!available) {
            throw new BusinessException("该时间段已被预约，请选择其他时间");
        }
        
        // 设置默认值
        appointment.setStatus(0); // 0-待确认
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointment.setDeleted(0);
        
        return appointmentMapper.insert(appointment) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Appointment appointment) {
        // 检查预约是否存在
        Appointment existAppointment = appointmentMapper.selectById(appointment.getId());
        if (existAppointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查预约状态
        if (existAppointment.getStatus() == 3 || existAppointment.getStatus() == 4) {
            throw new BusinessException("已完成或已取消的预约不能修改");
        }
        
        // 检查顾客是否存在
        Customer customer = customerMapper.selectById(appointment.getCustomerId());
        if (customer == null) {
            throw new BusinessException("顾客不存在");
        }
        
        // 检查技师是否存在
        Therapist therapist = therapistMapper.selectById(appointment.getTherapistId());
        if (therapist == null) {
            throw new BusinessException("技师不存在");
        }
        
        // 检查服务项目是否存在
        ServiceItem serviceItem = serviceItemMapper.selectById(appointment.getServiceId());
        if (serviceItem == null) {
            throw new BusinessException("服务项目不存在");
        }

        // 如果修改了时间或技师，需要检查时间段是否可预约
//        if (!existAppointment.getTherapistId().equals(appointment.getTherapistId()) ||
//                !existAppointment.getAppointmentDate().equals(appointment.getAppointmentDate()) ||
//                !existAppointment.getStartTime().equals(appointment.getStartTime()) ||
//                !existAppointment.getEndTime().equals(appointment.getEndTime())) {
//            boolean available = checkTimeSlotAvailable(appointment.getTherapistId(),
//                    appointment.getAppointmentDate(), appointment.getStartTime(), appointment.getEndTime());
//            if (!available) {
//                throw new BusinessException("该时间段已被预约，请选择其他时间");
//            }
//        }

        
        // 设置更新时间
        appointment.setUpdateTime(LocalDateTime.now());
        
        return appointmentMapper.updateById(appointment) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id, String cancelReason) {
        // 检查预约是否存在
        Appointment existAppointment = appointmentMapper.selectById(id);
        if (existAppointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查预约状态
        if (existAppointment.getStatus() == 2 || existAppointment.getStatus() == 3) {
            throw new BusinessException("已完成或已取消的预约不能取消");
        }
        
        // 更新预约状态
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus(3); // 3-已取消
        appointment.setCancelReason(cancelReason);
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        return appointmentMapper.updateById(appointment) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean complete(Long id) {
        // 检查预约是否存在
        Appointment existAppointment = appointmentMapper.selectById(id);
        if (existAppointment == null) {
            throw new BusinessException("预约不存在");
        }
        
        // 检查预约状态
        if (existAppointment.getStatus() == 2 || existAppointment.getStatus() == 3) {
            throw new BusinessException("已完成或已取消的预约不能完成");
        }
        
        // 更新预约状态
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus(2); // 2-已完成
        appointment.setUpdateTime(LocalDateTime.now());
        
        return appointmentMapper.updateById(appointment) > 0;
    }

    @Override
    public List<Map<String, Object>> getTherapistTimeSlots(Long therapistId, LocalDate date) {
        return appointmentMapper.selectTherapistTimeSlots(therapistId, date);
    }

    @Override
    public boolean checkTimeSlotAvailable(Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 检查技师在该时间段是否有其他预约
        Integer count = appointmentMapper.countConflictAppointments(therapistId, date, startTime, endTime);
        return count == 0;
    }

    @Override
    public Map<String, Object> getTodayStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日预约总数
        Integer totalCount = appointmentMapper.countTodayAppointments();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 今日待确认预约数
        Integer pendingCount = appointmentMapper.countTodayAppointmentsByStatus(0);
        stats.put("pendingCount", pendingCount != null ? pendingCount : 0);
        
        // 今日已确认预约数
        Integer confirmedCount = appointmentMapper.countTodayAppointmentsByStatus(1);
        stats.put("confirmedCount", confirmedCount != null ? confirmedCount : 0);
        
        // 今日已完成预约数
        Integer completedCount = appointmentMapper.countTodayAppointmentsByStatus(2);
        stats.put("completedCount", completedCount != null ? completedCount : 0);
        
        // 今日已取消预约数
        Integer canceledCount = appointmentMapper.countTodayAppointmentsByStatus(3);
        stats.put("canceledCount", canceledCount != null ? canceledCount : 0);
        
        return stats;
    }

    @Override
    public Map<String, Object> getWeekStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 未来一周每天的预约数
        List<Map<String, Object>> dailyStats = appointmentMapper.countWeekAppointments();
        stats.put("dailyStats", dailyStats);
        
        // 未来一周预约总数
        Integer totalCount = appointmentMapper.countWeekTotalAppointments();
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        return stats;
    }
}
