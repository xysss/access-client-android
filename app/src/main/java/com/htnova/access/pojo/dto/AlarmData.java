package com.htnova.access.pojo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AlarmData {
    // CWA报警处理 >毒性气体报警处理 >一般毒性气体报警处理 >烟感
    // 4-cwa报警，3-毒性气体报警，2-一般毒性气体报警，1-烟感报警。
    private int displayOrder;// 显示顺序：4最高排在最前，1最低排在最后。
    private String concentration;// 目标浓度。
    private String alarmContent;// 报警内容。
    private String sensorCode;// 传感器编号。
    private String sensorName;// 传感器名称。
    private Long beginTime = System.currentTimeMillis(); // 起始时间：长整型。
    private Long endTime = System.currentTimeMillis(); // 结束时间：长整型。
    private Date beginTimeD = new Date(); // 起始时间：日期型。
    private Date endTimeD = new Date(); // 结束时间：日期型。
}
