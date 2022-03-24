package com.htnova.access.pojo.po;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class SimDeviceData {
    private String id;
    private String sn;
    private String deviceType;
    private int simFlag;
    private String createBy;
    private LocalDateTime createDate;
    private String updateBy;
    private LocalDateTime updateDate;
    private String remarks;
    private String delFlag;
}
