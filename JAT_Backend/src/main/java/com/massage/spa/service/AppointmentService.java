package com.massage.spa.service;

import com.massage.spa.common.page.PageQuery;
import com.massage.spa.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 预约服务接口
 */
public interface AppointmentService {

    List<Map<String, Object>> listByPage(String customerId, String customerName, String therapistName,
                                        Long serviceId, Integer status, LocalDate fromDate, LocalDate toDate,
                                         PageQuery pageQuery);

    List<Map<String, Object>> getAppointmentsByDate(LocalDate fromDate, LocalDate toDate, Long therapistId);

    Long count(String customerId, String customerName, String therapistName,
              Long serviceId, Integer status, LocalDate fromDate, LocalDate toDate);

    /**
     * 根据ID获取预约
     * @param id 预约ID
     * @return 预约对象
     */
    Appointment getById(Long id);

    /**
     * 添加预约
     * @param appointment 预约对象
     * @return 是否成功
     */
    boolean add(Appointment appointment);

    /**
     * 更新预约
     * @param appointment 预约对象
     * @return 是否成功
     */
    boolean update(Appointment appointment);

    /**
     * 取消预约
     * @param id 预约ID
     * @param cancelReason 取消原因
     * @return 是否成功
     */
    boolean cancel(Long id, String cancelReason);

    /**
     * 完成预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean complete(Long id);
    
    /**
     * 获取技师某日的预约时间段
     * @param therapistId 技师ID
     * @param date 日期
     * @return 已预约的时间段列表
     */
    List<Map<String, Object>> getTherapistTimeSlots(Long therapistId, LocalDate date);
    
    /**
     * 检查时间段是否可预约
     * @param therapistId 技师ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否可预约
     */
    boolean checkTimeSlotAvailable(Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * 获取今日预约统计
     * @return 统计数据
     */
    Map<String, Object> getTodayStats();
    
    /**
     * 获取未来一周预约统计
     * @return 统计数据
     */
    Map<String, Object> getWeekStats();

    Map<String, Object> getAppointmentExtById(Long id);

    List<Map<String, Object>> getAppointmentExtByCustomerId(Long id);
}
