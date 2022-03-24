package com.htnova.access.pojo.dto;

import java.nio.charset.StandardCharsets;

import com.htnova.access.sysconfig.constdef.SensorTypeDictDef;

import lombok.Data;

@Data
public class SensorConfig {
    // 传感器序号，从0开始。
    // 针对FCBR-100M，针对TIC传感器，0-5为检测单元一，6-17为检测单元二。
    // 针对FCBR-100M，几种序号统一为一个列表，特约定CWA在原基础上加50。
    private byte sensorSeq;
    private byte sensorType;// 1-TIC传感器，2-CWA传感器。
    private String sensorSn;// 序列号。
    private int type;// 类型编码见下表，type为0表示传感器不在位。
    private int version;// 高字节主版本号，低字节次版本号。
    private String versionStr;// 以“.”分开的版本号。
    private String code = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_CODE;// 编码。
    private String name = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_NAME;// 传感器名称。
    private byte originUnit;// 原始单位：0-PPM，1-vol%，2-LEL%。
    private byte unit;// 当前单位：0-PPM，1-vol%，2-LEL%。
    private String unitName = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_ORIGINUNITNAME;// 单位：PPM，vol%，LEL%。
    private byte reserv;// 保留。
    private int molecularWeight;// 分子量。
    private int fullScall;// 量程。
    private float sensibility;// 精度。

    // CWA报警处理 >毒性气体报警处理 >一般毒性气体报警处理 >烟感
    // 4-cwa报警，3-毒性气体报警，2-一般毒性气体报警，1-烟感报警。
    private int displayOrder;// 显示顺序：4最高排在最前，1最低排在最后。

    /**
     * 根据名称字节数组，解析并设置code、name、displayOrder。
     *
     * @param nameBytes
     */
    public void parseAndSetNameCodeOrder(byte[] nameBytes) {
        String tempName = new String(nameBytes, StandardCharsets.UTF_8);
        if (tempName != null && tempName.length() > 0) {
            code = tempName.replaceAll("[\u0000]", "");
            name = SensorTypeDictDef.getName(code);
        } else {
            code = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_CODE;
            name = SensorTypeDictDef.SENSOR_TYPE_UNKNOW_NAME;
        }
        displayOrder = SensorTypeDictDef.getDisplayOrder(code);
    }
}
