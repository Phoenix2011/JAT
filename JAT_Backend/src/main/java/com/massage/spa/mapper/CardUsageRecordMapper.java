package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.CardUsageRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 卡项使用记录Mapper接口
 */
@Mapper
public interface CardUsageRecordMapper extends BaseMapper<CardUsageRecord> {
    
    /**
     * 查询卡项使用记录
     * @param cardId 卡项ID
     * @return 使用记录列表
     */
    @Select("SELECT cur.*, c.name as customer_name, s.name as service_name " +
            "FROM card_usage_record cur " +
            "LEFT JOIN customer c ON cur.customer_id = c.id " +
            "LEFT JOIN service_item s ON cur.service_id = s.id " +
            "WHERE cur.card_id = #{cardId} AND cur.deleted = 0 " +
            "ORDER BY cur.create_time DESC")
    List<Map<String, Object>> selectUsageRecordsByCardId(@Param("cardId") Long cardId);
}
