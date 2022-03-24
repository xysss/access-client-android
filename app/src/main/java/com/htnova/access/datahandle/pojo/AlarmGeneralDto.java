package com.htnova.access.datahandle.pojo;

import java.util.List;

import com.htnova.access.pojo.po.AlarmInfo;

import lombok.Data;

/** 报警信息数据传输对象，组合报警数据、报警信息等内容。 */
@Data
public class AlarmGeneralDto {
    private int transferFlag;
    private int riseAlarm;
    private AlarmInfo alarmInfo;
    private AlarmDataDto alarmData;
    private List<AlarmDataDto> alarmDataHis;
}
