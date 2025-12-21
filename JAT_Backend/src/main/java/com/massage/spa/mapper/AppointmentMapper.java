package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 预约Mapper接口
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    @Select("<script>" +
            "SELECT a.*, c.name as customer_name, c.phone as customer_phone, " +
            "t.name as therapist_name, s.name as service_name, " +
            "TIMESTAMPDIFF(MINUTE,start_time,end_time) as duration "+
            "FROM appointment a " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "LEFT JOIN therapist t ON a.therapist_id = t.id " +
            "LEFT JOIN service_item s ON a.service_id = s.id " +
            "WHERE a.deleted = 0 " +
            "<if test='customerId != null and customerId != \"\"'> AND c.id LIKE CONCAT('%', #{customerId}, '%') </if> " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='therapistName != null and therapistName != \"\"'> AND t.name LIKE CONCAT('%', #{therapistName}, '%') </if> " +
            "<if test='serviceId != null'> AND a.service_id = #{serviceId} </if> " +
            "<if test='status != null'> AND a.status = #{status} </if> " +
            "<if test='fromDate != null'> AND a.appointment_date between #{fromDate} and #{toDate} </if> " +
            "<if test='orderClause != null and orderClause != \"\"'> ORDER BY ${orderClause} </if> " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectAppointmentsByPage(@Param("customerId") String customerId,
                                                     @Param("customerName") String customerName,
                                                     @Param("therapistName") String therapistName,
                                                     @Param("serviceId") Long serviceId,
                                                     @Param("status") Integer status,
                                                     @Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate,
                                                     @Param("offset") Long offset,
                                                     @Param("limit") Long limit,
                                                     @Param("orderClause") String orderClause);


    @Select("<script>" +
            "SELECT a.*, c.name as customer_name, t.name as therapist_name, s.name as service_name " +
            "FROM appointment a " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "LEFT JOIN therapist t ON a.therapist_id = t.id " +
            "LEFT JOIN service_item s ON a.service_id = s.id " +
            "WHERE a.deleted = 0 " +
            "<if test='fromDate != null and toDate != null'> AND a.appointment_date BETWEEN #{fromDate} AND #{toDate} </if> " +
            "<if test='therapistId != null'> AND a.therapist_id = #{therapistId} </if> " +
            "ORDER BY a.appointment_date, a.start_time" +
            "</script>")
    List<Map<String, Object>> selectAppointmentsByDate(@Param("fromDate") LocalDate fromDate,
                                                       @Param("toDate") LocalDate toDate,
                                                       @Param("therapistId") Long therapistId);

    @Select("<script>" +
            "SELECT a.*, c.name as customer_name, c.phone as customer_phone, " +
            "t.name as therapist_name, s.name as service_name, " +
            "TIMESTAMPDIFF(MINUTE,start_time,end_time) as duration "+
            "FROM appointment a " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "LEFT JOIN therapist t ON a.therapist_id = t.id " +
            "LEFT JOIN service_item s ON a.service_id = s.id " +
            "WHERE a.deleted = 0 " +
            "<if test='appointmentID != null'> AND a.id = #{appointmentID} </if> " +
            "<if test='customerID != null'> AND a.customer_id = #{customerID} </if> " +
            "</script>")
    Map<String, Object> selectAppointmentExt(Long appointmentID, Long customerID);

    @Select("<script>" +
            "SELECT a.*, c.name as customer_name, c.phone as customer_phone, " +
            "t.name as therapist_name, s.name as service_name, " +
            "TIMESTAMPDIFF(MINUTE,start_time,end_time) as duration "+
            "FROM appointment a " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "LEFT JOIN therapist t ON a.therapist_id = t.id " +
            "LEFT JOIN service_item s ON a.service_id = s.id " +
            "WHERE a.deleted = 0 " +
            "<if test='appointmentID != null'> AND a.id = #{appointmentID} </if> " +
            "<if test='customerID != null'> AND a.customer_id = #{customerID} </if> " +
            "</script>")
    List<Map<String, Object>> selectAppointmentExtList(Long appointmentID, Long customerID);


    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM appointment a " +
            "LEFT JOIN customer c ON a.customer_id = c.id " +
            "LEFT JOIN therapist t ON a.therapist_id = t.id " +
            "LEFT JOIN service_item s ON a.service_id = s.id " +
            "WHERE a.deleted = 0 " +
            "<if test='customerId != null and customerId != \"\"'> AND c.id LIKE CONCAT('%', #{customerId}, '%') </if> " +
            "<if test='customerName != null and customerName != \"\"'> AND c.name LIKE CONCAT('%', #{customerName}, '%') </if> " +
            "<if test='therapistName != null and therapistName != \"\"'> AND t.name LIKE CONCAT('%', #{therapistName}, '%') </if> " +
            "<if test='serviceId != null'> AND a.service_id = #{serviceId} </if> " +
            "<if test='status != null'> AND a.status = #{status} </if> " +
            "<if test='fromDate != null'> AND a.appointment_date between #{fromDate} and #{toDate} </if> " +
            "</script>")
    Long countAppointments(@Param("customerId") String customerId,
                          @Param("customerName") String customerName,
                          @Param("therapistName") String therapistName,
                          @Param("serviceId") Long serviceId,
                          @Param("status") Integer status,
                           @Param("fromDate") LocalDate fromDate,
                           @Param("toDate") LocalDate toDate);

    /**
     * 查询技师某日的预约时间段
     * @param therapistId 技师ID
     * @param date 日期
     * @return 已预约的时间段列表
     */
    @Select("SELECT id, appointment_date, start_time, end_time, status " +
            "FROM appointment " +
            "WHERE therapist_id = #{therapistId} AND appointment_date = #{date} " +
            "AND status IN (0, 1) AND deleted = 0 " +
            "ORDER BY start_time")
    List<Map<String, Object>> selectTherapistTimeSlots(@Param("therapistId") Long therapistId,
                                                     @Param("date") LocalDate date);
    
    /**
     * 统计技师在某时间段内的冲突预约数量
     * @param therapistId 技师ID
     * @param date 日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突预约数量
     */
    @Select("SELECT COUNT(*) FROM appointment " +
            "WHERE therapist_id = #{therapistId} AND appointment_date = #{date} " +
            "AND status IN (0, 1) AND deleted = 0 " +
            "AND ((start_time <= #{startTime} AND end_time > #{startTime}) " +
            "OR (start_time < #{endTime} AND end_time >= #{endTime}) " +
            "OR (start_time >= #{startTime} AND end_time <= #{endTime}))")
    Integer countConflictAppointments(@Param("therapistId") Long therapistId,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);
    
    /**
     * 统计今日预约总数
     * @return 今日预约总数
     */
    @Select("SELECT COUNT(*) FROM appointment " +
            "WHERE appointment_date = CURDATE() AND deleted = 0")
    Integer countTodayAppointments();
    
    /**
     * 统计今日指定状态的预约数量
     * @param status 状态
     * @return 预约数量
     */
    @Select("SELECT COUNT(*) FROM appointment " +
            "WHERE appointment_date = CURDATE() AND status = #{status} AND deleted = 0")
    Integer countTodayAppointmentsByStatus(@Param("status") Integer status);
    
    /**
     * 统计未来一周每天的预约数量
     * @return 每天预约数量
     */
    @Select("SELECT appointment_date, COUNT(*) as count " +
            "FROM appointment " +
            "WHERE appointment_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
            "AND deleted = 0 " +
            "GROUP BY appointment_date " +
            "ORDER BY appointment_date")
    List<Map<String, Object>> countWeekAppointments();
    
    /**
     * 统计未来一周预约总数
     * @return 预约总数
     */
    @Select("SELECT COUNT(*) FROM appointment " +
            "WHERE appointment_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
            "AND deleted = 0")
    Integer countWeekTotalAppointments();


}
