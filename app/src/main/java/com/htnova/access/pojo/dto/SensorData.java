package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.htnova.access.sysconfig.constdef.AlarmDef;
import com.htnova.access.sysconfig.constdef.SensorTypeDictDef;

import lombok.Data;

@Data
public class SensorData {
    // 传感器序号，从0开始。
    // 针对FCBR-100M，针对TIC传感器，0-5为检测单元一，6-17为检测单元二。
    // 针对FCBR-100M，几种序号统一为一个列表，特约定CWA在原基础上加50。
    private byte sensorSeq;
    private byte sensorType;// 1-TIC传感器，2-CWA传感器。
    private String sensorSn;// 序列号。
    private byte installState;// 安装状态：0-未安装，1-安装，传感器编号从0开始。
    private byte runstate;// 运行状态：0-停止，1-运行。
    private byte faultState;// 故障状态：0-无故障，1-故障。
    private byte alarmState;// 报警状态：0-无报警，1-报警。
    // 报警类型等级:无报警列表长度为0，有报警按顺序排列的L、H、ST、TW、MA之一或多个。
    private List<String> alarmTypeLevels = new ArrayList<>();

    private int type;// 传感器类型编码，为0表示传感器不在位。
    private int version;// 高字节主版本号，低字节次版本号。
    private String versionStr;// 以“.”分开的版本号。

    private String code = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_CODE;// 编码。
    private String name = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_NAME;// 传感器名称。
    private byte originUnit;// 原始单位：0-PPM，1-vol%，2-LEL%。
    private byte unit;// 当前单位：0-PPM，1-vol%，2-LEL%。
    private String unitName = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_ORIGINUNITNAME;// 单位：PPM，vol%，LEL%。
    private int molecularWeight;// 分子量。
    private int fullScall;// 量程。
    private float sensibility;// 精度。

    private int originReading;// 原始读数，实际数据要根据小数点后的位数相除得出结果。
    private byte dotLength;// 小数点位数。
    private byte overflow;// 0-未溢出，1-溢出。
    private float originValue;// 原始单位对应的值，除以小数点位数之后的实际值。
    private float value;// 当前单位对应的值，除以小数点位数之后的实际值。
    private float bground;// 本底值。

    // 用于单个传感器数据的变化趋势判断。
    private String trend = AlarmDef.CONT_SMOOTH;

    // CWA报警处理 >毒性气体报警处理 >一般毒性气体报警处理 >烟感
    // 4-cwa报警，3-毒性气体报警，2-一般毒性气体报警，1-烟感报警。
    private int displayOrder;// 显示顺序：4最高排在最前，1最低排在最后。

    private Map<String, Float> currentParam;
    private Map<String, Float> default1Param;
    private Map<String, Float> default2Param;

    public void addAlarmTypeLevel(String alarmTypeLevel) {
        alarmTypeLevels.add(alarmTypeLevel);
    }
}
