package com.htnova.access.pojo.po;

import lombok.Data;

/** 参数项实体。 */
@Data
public class TableInfoItem {
    private String tableSchema;
    private String tableName;
    private String columnName;

    private String dataType;
    private String nullable;
    private String charLength;

    private String numericPrecision;
    private String datetimePrecision;
}
