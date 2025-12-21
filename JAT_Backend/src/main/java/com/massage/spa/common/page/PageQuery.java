package com.massage.spa.common.page;

import lombok.Data;

/**
 * 分页查询参数类
 */
@Data
public class PageQuery {

    /**
     * 当前页码，默认为1
     */
    private Long pageNum = 1L;

    /**
     * 每页记录数，默认为10
     */
    private Long pageSize = 10L;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 排序方式，true为升序，false为降序
     */
    private Boolean isAsc = true;
}
