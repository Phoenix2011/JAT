package com.massage.spa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.massage.spa.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 顾客Mapper接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}