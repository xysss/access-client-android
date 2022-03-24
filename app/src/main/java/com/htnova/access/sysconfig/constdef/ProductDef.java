package com.htnova.access.sysconfig.constdef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 产品定义。 */
public interface ProductDef {
    // 设备分类（按监测还是检测分类）：0-未定义，1-监测类设备，2-检测类设备。
    int RUN_MODE_UNDEF = 0;
    int RUN_MODE_MONITOR = 1;
    int RUN_MODE_CHECK = 2;

    int PRODUCT_CODE_308 = 0x05;

    String DEVICE_TYPE_FCBR100 = "FCBR-100";
    String DEVICE_TYPE_FCBR100PRO = "FCBR-100PRO";
    String DEVICE_TYPE_FCBR100B = "FCBR-100B";
    String DEVICE_TYPE_FCBR100C = "FCBR-100C";
    String DEVICE_TYPE_FCBR100S1 = "FCBR-100S1";
    String DEVICE_TYPE_FCBR100CP = "FCBR-100CP";
    String DEVICE_TYPE_FCBR100F = "FCBR-100F";
    String DEVICE_TYPE_FCBR100G = "FCBR-100G";
    String DEVICE_TYPE_FCBR100M = "FCBR-100M";
    String DEVICE_TYPE_FCBRv1 = "FCBRv1";
    String DEVICE_TYPE_RS500 = "RS500";
    String DEVICE_TYPE_GD606 = "GD606";
    String DEVICE_TYPE_BM3001 = "BM3001";

    List<Map<String, Object>> DEVICE_TYPES = new ArrayList<Map<String, Object>>() {
        {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100);
            tempMap.put("value", DEVICE_TYPE_FCBR100);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100PRO);
            tempMap.put("value", DEVICE_TYPE_FCBR100PRO);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100B);
            tempMap.put("value", DEVICE_TYPE_FCBR100B);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100C);
            tempMap.put("value", DEVICE_TYPE_FCBR100C);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100CP);
            tempMap.put("value", DEVICE_TYPE_FCBR100CP);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100F);
            tempMap.put("value", DEVICE_TYPE_FCBR100F);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBR100G);
            tempMap.put("value", DEVICE_TYPE_FCBR100G);
            add(tempMap);

            tempMap = new HashMap<>();
            tempMap.put("label", DEVICE_TYPE_FCBRv1);
            tempMap.put("value", DEVICE_TYPE_FCBRv1);
            add(tempMap);
        }
    };
}
