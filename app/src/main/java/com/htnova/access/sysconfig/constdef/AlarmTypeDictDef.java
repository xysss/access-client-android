package com.htnova.access.sysconfig.constdef;

import java.util.LinkedHashMap;
import java.util.Map;

/** 应用常量类，除了一些专门的常量（如：Mqtt、Mail、Datahub），都放到该类中。 */
public interface AlarmTypeDictDef {
    // 报警类型列表。
    String DICT_ALRAM_TYPE_START = "alarm_type";
    String ALARM_TYPE_NAME_SMOKE = "烟雾";
    String ALARM_TYPE_NAME_BIO = "生物因子";
    String ALARM_TYPE_NAME_CWA = "化学气体";
    String ALARM_TYPE_NAME_TVOC = "有毒有害易燃气体";
    String ALARM_TYPE_NAME_ABGAS = "异常气体";
    String ALARM_TYPE_NAME_NUCLEAR = "核辐射";
    String ALARM_TYPE_NAME_EC = "电化学";
    String ALARM_TYPE_NAME_GD606 = "复合气体报警";
    String ALARM_TYPE_NAME_UNKNOWN = "未知报警类型";
    String ALARM_TYPE_NAME_SAFETY = "安全";
    String ALARM_TYPE_NAME_CONTRABAND = "违禁品";
    String ALARM_TYPE_NAME_DANGER = "危险";

    Map<String, String> ALARM_TYPE_MONITOR = new LinkedHashMap<String, String>() {
        {
            put(ALARM_TYPE_NAME_UNKNOWN, "0");
            put(ALARM_TYPE_NAME_SMOKE, "101");
            put(ALARM_TYPE_NAME_BIO, "102");
            put(ALARM_TYPE_NAME_CWA, "103");
            put(ALARM_TYPE_NAME_TVOC, "104");
            put(ALARM_TYPE_NAME_ABGAS, "105");
            put(ALARM_TYPE_NAME_NUCLEAR, "106");
            put(ALARM_TYPE_NAME_EC, "107");
            put(ALARM_TYPE_NAME_GD606, "108");
        }
    };

    Map<String, String> ALARM_TYPE_CHECK = new LinkedHashMap<String, String>() {
        {
            put(ALARM_TYPE_NAME_UNKNOWN, "0");
            put(ALARM_TYPE_NAME_SAFETY, "201");
            put(ALARM_TYPE_NAME_CONTRABAND, "202");
            put(ALARM_TYPE_NAME_DANGER, "203");
        }
    };

    // 611系列报警状态：0-正常（绿色），1-预警（黄色），2-低报（红色），3-高报（紫色）。
    byte STATE_NORMAL = 0;
    byte STATE_PREALARM = 1;
    byte STATE_ALARM_L = 2;
    byte STATE_ALARM_H = 3;
}
