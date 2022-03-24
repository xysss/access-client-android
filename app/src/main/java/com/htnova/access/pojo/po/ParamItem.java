package com.htnova.access.pojo.po;

import lombok.Data;

/** 参数项实体。 */
@Data
public class ParamItem {
    private String id;
    private int batchId;
    private String code;
    private String lowValue;
    private String midValue;
    private String highValue;
    private int type;
    private int length;
    private int precision;
}
