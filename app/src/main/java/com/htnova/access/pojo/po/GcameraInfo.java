package com.htnova.access.pojo.po;

import java.util.Date;

import lombok.Data;

/** 伽马相机报警信息实体。 */
@Data
public class GcameraInfo {
    private String id;
    private String sn;
    private String gcameraId;
    private float radDose;
    private Date beginTimeD;
    private Long beginTime;
    private Date endTimeD;
    private Long endTime;
    private String dataFile;
}
