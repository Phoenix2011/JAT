package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.ServiceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 服务记录Mapper接口
 */
@Mapper
public interface ServiceRecordMapper extends BaseMapper<ServiceRecord> {
    
    /**
     * 查询技师服务次数
     * @param therapistId 技师ID
     * @return 服务次数
     */
    @Select("SELECT COUNT(*) FROM service_record WHERE therapist_id = #{therapistId} AND deleted = 0")
    Integer selectCountByTherapistId(@Param("therapistId") Long therapistId);
    
    /**
     * 查询技师服务总时长（分钟）
     * @param therapistId 技师ID
     * @return 服务总时长
     */
    @Select("SELECT SUM(duration) FROM service_record WHERE therapist_id = #{therapistId} AND deleted = 0")
    Integer selectTotalDurationByTherapistId(@Param("therapistId") Long therapistId);
    
    /**
     * 查询技师总提成金额
     * @param therapistId 技师ID
     * @return 总提成金额
     */
    @Select("SELECT SUM(commission_amount) FROM service_record WHERE therapist_id = #{therapistId} AND deleted = 0")
    Double selectTotalCommissionByTherapistId(@Param("therapistId") Long therapistId);
    
    /**
     * 查询技师最近服务时间
     * @param therapistId 技师ID
     * @return 最近服务时间
     */
    @Select("SELECT service_date FROM service_record WHERE therapist_id = #{therapistId} AND deleted = 0 ORDER BY service_date DESC, start_time DESC LIMIT 1")
    LocalDateTime selectLastServiceTimeByTherapistId(@Param("therapistId") Long therapistId);
    
    /**
     * 查询技师服务详情
     * @param therapistId 技师ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 服务详情列表
     */
    @Select("SELECT sr.*, c.name as customer_name, si.name as service_name " +
            "FROM service_record sr " +
            "LEFT JOIN customer c ON sr.customer_id = c.id " +
            "LEFT JOIN service_item si ON sr.service_id = si.id " +
            "WHERE sr.therapist_id = #{therapistId} AND sr.deleted = 0 " +
            "ORDER BY sr.service_date DESC, sr.start_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectServiceDetailsByTherapistId(@Param("therapistId") Long therapistId, 
                                                              @Param("offset") Long offset, 
                                                              @Param("limit") Long limit);
    
    /**
     * 查询技师提成详情
     * @param therapistId 技师ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 提成详情列表
     */
    @Select("SELECT sr.id, sr.service_date, sr.start_time, sr.end_time, sr.duration, " +
            "sr.amount, sr.commission_rate, sr.commission_amount, " +
            "c.name as customer_name, si.name as service_name " +
            "FROM service_record sr " +
            "LEFT JOIN customer c ON sr.customer_id = c.id " +
            "LEFT JOIN service_item si ON sr.service_id = si.id " +
            "WHERE sr.therapist_id = #{therapistId} AND sr.deleted = 0 " +
            "ORDER BY sr.service_date DESC, sr.start_time DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Map<String, Object>> selectCommissionDetailsByTherapistId(@Param("therapistId") Long therapistId, 
                                                                 @Param("offset") Long offset, 
                                                                 @Param("limit") Long limit);
}
