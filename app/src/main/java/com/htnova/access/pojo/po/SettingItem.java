package com.htnova.access.pojo.po;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/** 系统设置项：用了RequestBody之后，C#端的属性需要为小写，与这里一致，如果C#以Id、Code、Name命名属性，会映射不到。 */
@Data
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class SettingItem {
    private String id;
    private String code;
    private String name;
}
