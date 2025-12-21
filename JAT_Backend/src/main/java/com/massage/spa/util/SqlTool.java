package com.massage.spa.util;

import org.apache.commons.lang3.StringUtils;

public class SqlTool {
    
    public static String generateOrderClause(String sortField, Boolean isAsc) {
        // 处理排序字段
        String orderClause = null;
        if (sortField == null || sortField.isEmpty()) {
            orderClause = "a.appointment_date DESC, a.start_time DESC";
        } else {
            // 自定义的排序字段，中间含逗号的则需要拆分，分别加排序规则
            String orderType = isAsc ? "ASC" : "DESC";
            if (sortField.contains(",")) {
                String[] fields = sortField.split(",");
                StringBuilder sb = new StringBuilder();
                for (String field : fields) {
                    if (StringUtils.isNotBlank(field)) {
                        sb.append("a.").append(field.trim()).append(" ").append(orderType).append(", ");
                    }
                }
                if (!sb.isEmpty()) {
                    sb.setLength(sb.length() - 2); // 去掉最后的逗号和空格
                    orderClause = sb.toString();
                }
            } else {
                orderClause = "a." + sortField + " " + orderType;
            }
        }
        return orderClause;
    }
    
}
