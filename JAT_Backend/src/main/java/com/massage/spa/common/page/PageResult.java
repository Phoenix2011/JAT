package com.massage.spa.common.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 每页记录数
     */
    private Long pageSize;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 构造函数
     */
    public PageResult() {
    }

    /**
     * 构造函数
     * @param total 总记录数
     * @param size 每页记录数
     * @param current 当前页码
     * @param records 数据列表
     */
    public PageResult(Long total, Long size, Long current, List<T> records) {
        this.total = total;
        this.pageSize = size;
        this.pageNum = current;
        this.records = records;
        this.pages = total % size == 0 ? total / size : total / size + 1;
    }
}
