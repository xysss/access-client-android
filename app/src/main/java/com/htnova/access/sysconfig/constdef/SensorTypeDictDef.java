package com.htnova.access.sysconfig.constdef;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/** 应用常量类，除了一些专门的常量（如：Mqtt、Mail、Datahub），都放到该类中。 */
public interface SensorTypeDictDef {
    // 传感器类型：1-TIC，2-CWA。
    byte SENSOR_TIC = 1;
    byte SENSOR_CWA = 2;

    // 传感器场景：0-工作场景，1-公共场景。
    byte SCENE_WORK = 0;
    byte SCENE_PUBLIC = 1;

    // 传感器类型列表。
    String SENSOR_TYPE_O2_CODE = "O2";
    String SENSOR_TYPE_O2_NAME = "氧气";
    int SENSOR_TYPE_O2_DISPLAYORDER = 2;
    byte SENSOR_TYPE_O2_ORIGINUNIT = 0;

    String SENSOR_TYPE_H2S_CODE = "H2S";
    String SENSOR_TYPE_H2S_NAME = "硫化氢";
    int SENSOR_TYPE_H2S_DISPLAYORDER = 3;
    byte SENSOR_TYPE_H2S_ORIGINUNIT = 0;

    String SENSOR_TYPE_NO2_CODE = "NO2";
    String SENSOR_TYPE_NO2_NAME = "二氧化氮";
    int SENSOR_TYPE_NO2_DISPLAYORDER = 3;
    byte SENSOR_TYPE_NO2_ORIGINUNIT = 0;

    String SENSOR_TYPE_CO_CODE = "CO";
    String SENSOR_TYPE_CO_NAME = "一氧化碳";
    int SENSOR_TYPE_CO_DISPLAYORDER = 3;
    byte SENSOR_TYPE_CO_ORIGINUNIT = 0;

    String SENSOR_TYPE_SO2_CODE = "SO2";
    String SENSOR_TYPE_SO2_NAME = "二氧化硫";
    int SENSOR_TYPE_SO2_DISPLAYORDER = 3;
    byte SENSOR_TYPE_SO2_ORIGINUNIT = 0;

    String SENSOR_TYPE_NH3_CODE = "NH3";
    String SENSOR_TYPE_NH3_NAME = "氨气";
    int SENSOR_TYPE_NH3_DISPLAYORDER = 3;
    byte SENSOR_TYPE_NH3_ORIGINUNIT = 0;

    String SENSOR_TYPE_CL2_CODE = "CL2";
    String SENSOR_TYPE_CL2_NAME = "氯气";
    int SENSOR_TYPE_CL2_DISPLAYORDER = 3;
    byte SENSOR_TYPE_CL2_ORIGINUNIT = 0;

    String SENSOR_TYPE_H2_CODE = "H2";
    String SENSOR_TYPE_H2_NAME = "氢气";
    int SENSOR_TYPE_H2_DISPLAYORDER = 3;
    byte SENSOR_TYPE_H2_ORIGINUNIT = 0;

    String SENSOR_TYPE_HCL_CODE = "HCL";
    String SENSOR_TYPE_HCL_NAME = "氯化氢";
    int SENSOR_TYPE_HCL_DISPLAYORDER = 3;
    byte SENSOR_TYPE_HCL_ORIGINUNIT = 0;

    String SENSOR_TYPE_PH3_CODE = "PH3";
    String SENSOR_TYPE_PH3_NAME = "磷化氢";
    int SENSOR_TYPE_PH3_DISPLAYORDER = 3;
    byte SENSOR_TYPE_PH3_ORIGINUNIT = 0;

    String SENSOR_TYPE_O3_CODE = "O3";
    String SENSOR_TYPE_O3_NAME = "臭氧";
    int SENSOR_TYPE_O3_DISPLAYORDER = 3;
    byte SENSOR_TYPE_O3_ORIGINUNIT = 0;

    String SENSOR_TYPE_HCN_CODE = "HCN";
    String SENSOR_TYPE_HCN_NAME = "氰化氢";
    int SENSOR_TYPE_HCN_DISPLAYORDER = 3;
    byte SENSOR_TYPE_HCN_ORIGINUNIT = 0;

    String SENSOR_TYPE_CH2O_CODE = "CH2O";
    String SENSOR_TYPE_CH2O_NAME = "甲醛";
    int SENSOR_TYPE_CH2O_DISPLAYORDER = 2;
    byte SENSOR_TYPE_CH2O_ORIGINUNIT = 0;

    String SENSOR_TYPE_NO_CODE = "NO";
    String SENSOR_TYPE_NO_NAME = "一氧化氮";
    int SENSOR_TYPE_NO_DISPLAYORDER = 3;
    byte SENSOR_TYPE_NO_ORIGINUNIT = 0;

    String SENSOR_TYPE_VOC1_CODE = "VOC";
    String SENSOR_TYPE_VOC1_NAME = "TVOC";
    int SENSOR_TYPE_VOC1_DISPLAYORDER = 2;
    byte SENSOR_TYPE_VOC1_ORIGINUNIT = 0;

    String SENSOR_TYPE_VOC2_CODE = "VOC-10.0ev";
    String SENSOR_TYPE_VOC2_NAME = "苯系物";
    int SENSOR_TYPE_VOC2_DISPLAYORDER = 2;
    byte SENSOR_TYPE_VOC2_ORIGINUNIT = 0;

    String SENSOR_TYPE_CH4_CODE = "CH4";
    String SENSOR_TYPE_CH4_NAME = "甲烷";
    int SENSOR_TYPE_CH4_DISPLAYORDER = 3;
    byte SENSOR_TYPE_CH4_ORIGINUNIT = 0;

    String SENSOR_TYPE_CO2_CODE = "CO2";
    String SENSOR_TYPE_CO2_NAME = "二氧化碳";
    int SENSOR_TYPE_CO2_DISPLAYORDER = 2;
    byte SENSOR_TYPE_CO2_ORIGINUNIT = 0;

    String SENSOR_TYPE_LEWISITE_CODE = "L";
    String SENSOR_TYPE_LEWISITE_NAME = "路易氏气";
    int SENSOR_TYPE_LEWISITE_DISPLAYORDER = 4;
    byte SENSOR_TYPE_LEWISITE_ORIGINUNIT = 4;

    String SENSOR_TYPE_VX_CODE = "VX";
    String SENSOR_TYPE_VX_NAME = "维埃克斯";
    int SENSOR_TYPE_VX_DISPLAYORDER = 4;
    byte SENSOR_TYPE_VX_ORIGINUNIT = 4;

    String SENSOR_TYPE_GB_CODE = "GB";
    String SENSOR_TYPE_GB_NAME = "沙林";
    int SENSOR_TYPE_GB_DISPLAYORDER = 4;
    byte SENSOR_TYPE_GB_ORIGINUNIT = 4;

    String SENSOR_TYPE_GD_CODE = "GD";
    String SENSOR_TYPE_GD_NAME = "梭曼";
    int SENSOR_TYPE_GD_DISPLAYORDER = 4;
    byte SENSOR_TYPE_GD_ORIGINUNIT = 4;

    String SENSOR_TYPE_HD_CODE = "HD";
    String SENSOR_TYPE_HD_NAME = "芥子气";
    int SENSOR_TYPE_HD_DISPLAYORDER = 4;
    byte SENSOR_TYPE_HD_ORIGINUNIT = 4;

    String SENSOR_TYPE_AC_CODE = "AC";
    String SENSOR_TYPE_AC_NAME = "氢氰酸";
    int SENSOR_TYPE_AC_DISPLAYORDER = 4;
    byte SENSOR_TYPE_AC_ORIGINUNIT = 4;

    String SENSOR_TYPE_CG_CODE = "CG";
    String SENSOR_TYPE_CG_NAME = "光气";
    int SENSOR_TYPE_CG_DISPLAYORDER = 4;
    byte SENSOR_TYPE_CG_ORIGINUNIT = 4;

    String SENSOR_TYPE_COCL2_CODE = "COCL2";
    String SENSOR_TYPE_COCL2_NAME = "光气";
    int SENSOR_TYPE_COCL2_DISPLAYORDER = 4;
    byte SENSOR_TYPE_COCL2_ORIGINUNIT = 4;

    String SENSOR_TYPE_SMOKE_CODE = "SMOKE";
    String SENSOR_TYPE_SMOKE_NAME = "烟气";
    int SENSOR_TYPE_SMOKE_DISPLAYORDER = 1;
    byte SENSOR_TYPE_SMOKE_ORIGINUNIT = 0;

    String SENSOR_TYPE_UNKNOW_CODE = "UNKNOW";
    String SENSOR_TYPE_UNKNOW_NAME = "未知类型";
    int SENSOR_TYPE_UNKNOW_DISPLAYORDER = 1;
    byte SENSOR_TYPE_UNKNOW_ORIGINUNIT = 0;
    String SENSOR_TYPE_UNKNOW_ORIGINUNITNAME = "PPM";

    Map<String, String> SENSOR_TYPE = new LinkedHashMap<String, String>() {
        {
            put(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_O2_NAME);

            put(SENSOR_TYPE_H2S_CODE, SENSOR_TYPE_H2S_NAME);

            put(SENSOR_TYPE_NO2_CODE, SENSOR_TYPE_NO2_NAME);

            put(SENSOR_TYPE_CO_CODE, SENSOR_TYPE_CO_NAME);

            put(SENSOR_TYPE_SO2_CODE, SENSOR_TYPE_SO2_NAME);

            put(SENSOR_TYPE_NH3_CODE, SENSOR_TYPE_NH3_NAME);

            put(SENSOR_TYPE_CL2_CODE, SENSOR_TYPE_CL2_NAME);

            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2_NAME);

            put(SENSOR_TYPE_HCL_CODE, SENSOR_TYPE_HCL_NAME);

            put(SENSOR_TYPE_PH3_CODE, SENSOR_TYPE_PH3_NAME);

            put(SENSOR_TYPE_O3_CODE, SENSOR_TYPE_O3_NAME);

            put(SENSOR_TYPE_HCN_CODE, SENSOR_TYPE_HCN_NAME);

            put(SENSOR_TYPE_CH2O_CODE, SENSOR_TYPE_CH2O_NAME);

            put(SENSOR_TYPE_NO_CODE, SENSOR_TYPE_NO_NAME);

            put(SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC1_NAME);

            put(SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_VOC2_NAME);

            put(SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CH4_NAME);

            put(SENSOR_TYPE_CO2_CODE, SENSOR_TYPE_CO2_NAME);

            put(SENSOR_TYPE_LEWISITE_CODE, SENSOR_TYPE_LEWISITE_NAME);

            put(SENSOR_TYPE_VX_CODE, SENSOR_TYPE_VX_NAME);

            put(SENSOR_TYPE_GB_CODE, SENSOR_TYPE_GB_NAME);

            put(SENSOR_TYPE_GD_CODE, SENSOR_TYPE_GD_NAME);

            put(SENSOR_TYPE_HD_CODE, SENSOR_TYPE_HD_NAME);

            put(SENSOR_TYPE_AC_CODE, SENSOR_TYPE_AC_NAME);

            put(SENSOR_TYPE_CG_CODE, SENSOR_TYPE_CG_NAME);

            put(SENSOR_TYPE_COCL2_CODE, SENSOR_TYPE_COCL2_NAME);

            put(SENSOR_TYPE_SMOKE_CODE, SENSOR_TYPE_SMOKE_NAME);
        }
    };
    Map<String, Integer> SENSOR_DISPLAYORDER = new LinkedHashMap<String, Integer>() {
        {
            put(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_O2_DISPLAYORDER);

            put(SENSOR_TYPE_H2S_CODE, SENSOR_TYPE_H2S_DISPLAYORDER);

            put(SENSOR_TYPE_NO2_CODE, SENSOR_TYPE_NO2_DISPLAYORDER);

            put(SENSOR_TYPE_CO_CODE, SENSOR_TYPE_CO_DISPLAYORDER);

            put(SENSOR_TYPE_SO2_CODE, SENSOR_TYPE_SO2_DISPLAYORDER);

            put(SENSOR_TYPE_NH3_CODE, SENSOR_TYPE_NH3_DISPLAYORDER);

            put(SENSOR_TYPE_CL2_CODE, SENSOR_TYPE_CL2_DISPLAYORDER);

            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2_DISPLAYORDER);

            put(SENSOR_TYPE_HCL_CODE, SENSOR_TYPE_HCL_DISPLAYORDER);

            put(SENSOR_TYPE_PH3_CODE, SENSOR_TYPE_PH3_DISPLAYORDER);

            put(SENSOR_TYPE_O3_CODE, SENSOR_TYPE_O3_DISPLAYORDER);

            put(SENSOR_TYPE_HCN_CODE, SENSOR_TYPE_HCN_DISPLAYORDER);

            put(SENSOR_TYPE_CH2O_CODE, SENSOR_TYPE_CH2O_DISPLAYORDER);

            put(SENSOR_TYPE_NO_CODE, SENSOR_TYPE_NO_DISPLAYORDER);

            put(SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC1_DISPLAYORDER);

            put(SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_VOC2_DISPLAYORDER);

            put(SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CH4_DISPLAYORDER);

            put(SENSOR_TYPE_CO2_CODE, SENSOR_TYPE_CO2_DISPLAYORDER);

            put(SENSOR_TYPE_LEWISITE_CODE, SENSOR_TYPE_LEWISITE_DISPLAYORDER);

            put(SENSOR_TYPE_VX_CODE, SENSOR_TYPE_VX_DISPLAYORDER);

            put(SENSOR_TYPE_GB_CODE, SENSOR_TYPE_GB_DISPLAYORDER);

            put(SENSOR_TYPE_GD_CODE, SENSOR_TYPE_GD_DISPLAYORDER);

            put(SENSOR_TYPE_HD_CODE, SENSOR_TYPE_HD_DISPLAYORDER);

            put(SENSOR_TYPE_AC_CODE, SENSOR_TYPE_AC_DISPLAYORDER);

            put(SENSOR_TYPE_CG_CODE, SENSOR_TYPE_CG_DISPLAYORDER);

            put(SENSOR_TYPE_COCL2_CODE, SENSOR_TYPE_COCL2_DISPLAYORDER);

            put(SENSOR_TYPE_SMOKE_CODE, SENSOR_TYPE_SMOKE_DISPLAYORDER);
        }
    };
    Map<String, Byte> SENSOR_ORIGINUNIT = new LinkedHashMap<String, Byte>() {
        {
            put(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_O2_ORIGINUNIT);

            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2S_ORIGINUNIT);

            put(SENSOR_TYPE_NO2_CODE, SENSOR_TYPE_NO2_ORIGINUNIT);

            put(SENSOR_TYPE_CO_CODE, SENSOR_TYPE_CO_ORIGINUNIT);

            put(SENSOR_TYPE_SO2_CODE, SENSOR_TYPE_SO2_ORIGINUNIT);

            put(SENSOR_TYPE_NH3_CODE, SENSOR_TYPE_NH3_ORIGINUNIT);

            put(SENSOR_TYPE_CL2_CODE, SENSOR_TYPE_CL2_ORIGINUNIT);

            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2_ORIGINUNIT);

            put(SENSOR_TYPE_HCL_CODE, SENSOR_TYPE_HCL_ORIGINUNIT);

            put(SENSOR_TYPE_PH3_CODE, SENSOR_TYPE_PH3_ORIGINUNIT);

            put(SENSOR_TYPE_O3_CODE, SENSOR_TYPE_O3_ORIGINUNIT);

            put(SENSOR_TYPE_HCN_CODE, SENSOR_TYPE_HCN_ORIGINUNIT);

            put(SENSOR_TYPE_CH2O_CODE, SENSOR_TYPE_CH2O_ORIGINUNIT);

            put(SENSOR_TYPE_NO_CODE, SENSOR_TYPE_NO_ORIGINUNIT);

            put(SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC1_ORIGINUNIT);

            put(SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_VOC2_ORIGINUNIT);

            put(SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CH4_ORIGINUNIT);

            put(SENSOR_TYPE_CO2_CODE, SENSOR_TYPE_CO2_ORIGINUNIT);

            put(SENSOR_TYPE_LEWISITE_CODE, SENSOR_TYPE_LEWISITE_ORIGINUNIT);

            put(SENSOR_TYPE_VX_CODE, SENSOR_TYPE_VX_ORIGINUNIT);

            put(SENSOR_TYPE_GB_CODE, SENSOR_TYPE_GB_ORIGINUNIT);

            put(SENSOR_TYPE_GD_CODE, SENSOR_TYPE_GD_ORIGINUNIT);

            put(SENSOR_TYPE_HD_CODE, SENSOR_TYPE_HD_ORIGINUNIT);

            put(SENSOR_TYPE_AC_CODE, SENSOR_TYPE_AC_ORIGINUNIT);

            put(SENSOR_TYPE_CG_CODE, SENSOR_TYPE_CG_ORIGINUNIT);

            put(SENSOR_TYPE_COCL2_CODE, SENSOR_TYPE_COCL2_ORIGINUNIT);

            put(SENSOR_TYPE_SMOKE_CODE, SENSOR_TYPE_SMOKE_ORIGINUNIT);
        }
    };

    /**
     * <pre>
     *     从嵌入式底层，传感器分为两部分，1-TIC，2-CWA。
     *     从前端显示，CWA保持不变，但将TIC拆分为两部分：
     *     （1）一部分为有毒有害气体；
     *     （2）另一部分为空气质量检测（动态增加SMOKE，SMOKE采用原有的解析方式，与通用传感器解析不同）。
     * </pre>
     */
    Map<String, String> TYPE_TIC_CODES = new LinkedHashMap<String, String>() {
        {
            put(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_O2_CODE);
            put(SENSOR_TYPE_H2S_CODE, SENSOR_TYPE_H2S_CODE);
            put(SENSOR_TYPE_NO2_CODE, SENSOR_TYPE_NO2_CODE);
            put(SENSOR_TYPE_CO_CODE, SENSOR_TYPE_CO_CODE);
            put(SENSOR_TYPE_SO2_CODE, SENSOR_TYPE_SO2_CODE);
            put(SENSOR_TYPE_NH3_CODE, SENSOR_TYPE_NH3_CODE);
            put(SENSOR_TYPE_CL2_CODE, SENSOR_TYPE_CL2_CODE);
            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2_CODE);
            put(SENSOR_TYPE_HCL_CODE, SENSOR_TYPE_HCL_CODE);
            put(SENSOR_TYPE_PH3_CODE, SENSOR_TYPE_PH3_CODE);
            put(SENSOR_TYPE_O3_CODE, SENSOR_TYPE_O3_CODE);
            put(SENSOR_TYPE_HCN_CODE, SENSOR_TYPE_HCN_CODE);
            put(SENSOR_TYPE_CH2O_CODE, SENSOR_TYPE_CH2O_CODE);
            put(SENSOR_TYPE_NO_CODE, SENSOR_TYPE_NO_CODE);
            put(SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC1_CODE);
            put(SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_VOC2_CODE);
            put(SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CH4_CODE);
            put(SENSOR_TYPE_CO2_CODE, SENSOR_TYPE_CO2_CODE);
            put(SENSOR_TYPE_SMOKE_CODE, SENSOR_TYPE_SMOKE_CODE);
        }
    };

    Map<String, String> TYPE_TIC_CODES_SMOKE = new LinkedHashMap<String, String>() {
        {
            put(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_O2_CODE);
            put(SENSOR_TYPE_CH2O_CODE, SENSOR_TYPE_CH2O_CODE);
            put(SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC1_CODE);
            put(SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_VOC2_CODE);
            put(SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CH4_CODE);
            put(SENSOR_TYPE_CO2_CODE, SENSOR_TYPE_CO2_CODE);
            put(SENSOR_TYPE_SMOKE_CODE, SENSOR_TYPE_SMOKE_CODE);

        }
    };

    Map<String, String> TYPE_TIC_CODES_TVOC = new LinkedHashMap<String, String>() {
        {
            put(SENSOR_TYPE_H2S_CODE, SENSOR_TYPE_H2S_CODE);
            put(SENSOR_TYPE_NO2_CODE, SENSOR_TYPE_NO2_CODE);
            put(SENSOR_TYPE_CO_CODE, SENSOR_TYPE_CO_CODE);
            put(SENSOR_TYPE_SO2_CODE, SENSOR_TYPE_SO2_CODE);
            put(SENSOR_TYPE_NH3_CODE, SENSOR_TYPE_NH3_CODE);
            put(SENSOR_TYPE_CL2_CODE, SENSOR_TYPE_CL2_CODE);
            put(SENSOR_TYPE_H2_CODE, SENSOR_TYPE_H2_CODE);
            put(SENSOR_TYPE_HCL_CODE, SENSOR_TYPE_HCL_CODE);
            put(SENSOR_TYPE_PH3_CODE, SENSOR_TYPE_PH3_CODE);
            put(SENSOR_TYPE_O3_CODE, SENSOR_TYPE_O3_CODE);
            put(SENSOR_TYPE_HCN_CODE, SENSOR_TYPE_HCN_CODE);
            put(SENSOR_TYPE_NO_CODE, SENSOR_TYPE_NO_CODE);
        }
    };

    Map<String, String> TYPE_CWA_CODES = new LinkedHashMap<String, String>() {
        {
            put(SENSOR_TYPE_LEWISITE_CODE, SENSOR_TYPE_LEWISITE_CODE);
            put(SENSOR_TYPE_VX_CODE, SENSOR_TYPE_VX_CODE);
            put(SENSOR_TYPE_GB_CODE, SENSOR_TYPE_GB_CODE);
            put(SENSOR_TYPE_GD_CODE, SENSOR_TYPE_GD_CODE);
            put(SENSOR_TYPE_HD_CODE, SENSOR_TYPE_HD_CODE);
            put(SENSOR_TYPE_AC_CODE, SENSOR_TYPE_AC_CODE);
            put(SENSOR_TYPE_CG_CODE, SENSOR_TYPE_CG_CODE);
        }
    };

    List<String> TYPE_TIC_TO_SMOKE_CODES = Lists.newArrayList(SENSOR_TYPE_O2_CODE, SENSOR_TYPE_CH2O_CODE,
        SENSOR_TYPE_VOC1_CODE, SENSOR_TYPE_VOC2_CODE, SENSOR_TYPE_CH4_CODE, SENSOR_TYPE_CO2_CODE);

    // TIC传感器：o2HighThreshold、o2LowThreshold、o2StelThreshold、o2TwaThreshold、o2MacThreshold、o2Unit。
    // CWA传感器：hdHighThreshold、hdUnit。
    String PARAM_KEY_HIGH = "HighThreshold";
    String PARAM_KEY_LOW = "LowThreshold";
    String PARAM_KEY_STEL = "StelThreshold";
    String PARAM_KEY_TWA = "TwaThreshold";
    String PARAM_KEY_MAC = "MacThreshold";
    String PARAM_KEY_UNIT = "Unit";
    String PARAM_VALUE_HIGH = "H";
    String PARAM_VALUE_LOW = "L";
    String PARAM_VALUE_STEL = "ST";
    String PARAM_VALUE_TWA = "TW";
    String PARAM_VALUE_MAC = "M";
    String PARAM_VALUE_UNIT = "U";
    Map<String, String> PARAM_SETTING_CODES = new LinkedHashMap<String, String>() {
        {
            put(PARAM_KEY_HIGH, PARAM_VALUE_HIGH);
            put(PARAM_KEY_LOW, PARAM_VALUE_LOW);
            put(PARAM_KEY_STEL, PARAM_VALUE_STEL);
            put(PARAM_KEY_TWA, PARAM_VALUE_TWA);
            put(PARAM_KEY_MAC, PARAM_VALUE_MAC);
            put(PARAM_KEY_UNIT, PARAM_VALUE_UNIT);
        }
    };

    List<String> PARAM_SETTING_KEYS =
        Lists.newArrayList(PARAM_KEY_HIGH, PARAM_KEY_LOW, PARAM_KEY_STEL, PARAM_KEY_TWA, PARAM_KEY_MAC, PARAM_KEY_UNIT);

    static String getName(String code) {
        if (SENSOR_TYPE.containsKey(code)) {
            return (SENSOR_TYPE.get(code));
        }

        return SENSOR_TYPE_UNKNOW_NAME;
    }

    static int getDisplayOrder(String code) {
        if (SENSOR_DISPLAYORDER.containsKey(code)) {
            return (SENSOR_DISPLAYORDER.get(code));
        }
        return SENSOR_TYPE_UNKNOW_DISPLAYORDER;
    }

    static byte getOriginUnit(String code) {
        if (SENSOR_ORIGINUNIT.containsKey(code)) {
            return (SENSOR_ORIGINUNIT.get(code));
        }
        return SENSOR_TYPE_UNKNOW_ORIGINUNIT;
    }
}