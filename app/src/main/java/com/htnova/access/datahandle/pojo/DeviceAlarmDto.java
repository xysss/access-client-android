package com.htnova.access.datahandle.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

/** 监测点事件数据，包含详细的原始数据列表。 */
@Data
public class DeviceAlarmDto {
    private String id;
    private String sn;
    private String outterSn;
    private String deviceName;
    private String deviceType;
    private String deviceTypeName;
    private String officeId;
    private String officeName;
    private String pointId;
    private String pointName;
    private String taskId; // 任务ID
    private String taskName; // 任务名称
    private Long beginTime;
    private Long endTime;
    private int state;
    private String typeName;
    private String typeCode;
    private String message;
    private Boolean newEvent; // 是否是新的警报：true-新，false-旧。

    // 支持多个报警同时保存：一段时间以来的所有报警，解决当前只保存最后一个报警的问题。
    private Map<String, Boolean> alarmContentMap;
    private Map<String, Boolean> alarmTypeNameMap;

    public void addAlarmContent(String content) {
        if (alarmContentMap == null) {
            alarmContentMap = new ConcurrentHashMap<>();
        }

        if (content == null) {
            return;
        }

        String[] contentArr = content.trim().split(",");
        if (contentArr == null || contentArr.length == 0) {
            return;
        }

        for (int i = 0; i < contentArr.length; i++) {
            String tempContent = contentArr[i];
            if (tempContent == null) {
                continue;
            }

            tempContent = tempContent.trim();
            if (tempContent.length() > 1) {
                alarmContentMap.put(tempContent, Boolean.TRUE);
            }
        }
    }

    public void addAlarmTypeName(String alarmTypeName) {
        if (alarmTypeNameMap == null) {
            alarmTypeNameMap = new ConcurrentHashMap<>();
        }

        if (alarmTypeName == null) {
            return;
        }

        alarmTypeName = alarmTypeName.trim();
        if (alarmTypeName.length() > 1) {
            alarmTypeNameMap.put(alarmTypeName, Boolean.TRUE);
        }
    }

    public String getMessageFromMap() {
        if (alarmContentMap != null && alarmContentMap.size() > 0) {
            StringBuilder buf = new StringBuilder("");
            alarmContentMap.forEach((tempKey, tempValue) -> {
                buf.append(tempKey).append(",");
            });

            if (buf.length() > 2) {
                String alarmContent = buf.substring(0, buf.length() - 1);
                return alarmContent;
            }
        }

        return message;
    }

    public String getAlarmTypeNameFromMap() {
        if (alarmTypeNameMap != null && alarmTypeNameMap.size() > 0) {
            StringBuilder buf = new StringBuilder("");
            alarmTypeNameMap.forEach((tempKey, tempValue) -> {
                buf.append(tempKey).append(",");
            });

            if (buf.length() > 2) {
                String alarmContent = buf.substring(0, buf.length() - 1);
                return alarmContent;
            }
        }

        return typeName;
    }
}
