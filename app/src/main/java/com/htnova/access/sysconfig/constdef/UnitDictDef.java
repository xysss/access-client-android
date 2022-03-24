package com.htnova.access.sysconfig.constdef;

import java.util.LinkedHashMap;
import java.util.Map;

/** 应用常量类，除了一些专门的常量（如：Mqtt、Mail、Datahub），都放到该类中。 */
public interface UnitDictDef {
    // 单位类型列表。
    Byte UNIT_CODE_PPM = 0;
    Byte UNIT_CODE_VOL = 1;
    Byte UNIT_CODE_LEL = 2;
    Byte UNIT_CODE_MGM3 = 3;
    Byte UNIT_CODE_PPB = 4;
    Byte UNIT_CODE_UNKNOWN = -1;
    Map<Byte, String> UNIT_MAP = new LinkedHashMap<Byte, String>() {
        {
            // 单位，0-PPM，1-vol%，2-LEL%，3-mg/m3，4-PPB。
            put(UNIT_CODE_PPM, "PPM");
            put(UNIT_CODE_VOL, "vol%");
            put(UNIT_CODE_LEL, "LEL%");
            put(UNIT_CODE_MGM3, "mg/m3");
            put(UNIT_CODE_PPB, "PPB");
            put(UNIT_CODE_UNKNOWN, "未知");
        }
    };

    static String getUnitName(byte unitCode) {
        if (UNIT_MAP.containsKey(unitCode)) {
            return UNIT_MAP.get(unitCode);
        }
        return UNIT_MAP.get(UNIT_CODE_UNKNOWN);
    }
}
