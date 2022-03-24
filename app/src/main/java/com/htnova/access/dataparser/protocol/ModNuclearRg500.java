package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.ModNuclearDto;

/** Nuclear模块的协议解析实现类。 */
class ModNuclearRg500 {
    void handleVer1(ModNuclearDto nuclearMod, byte[] sensorData, int msgId) throws Exception {
        float _fvalue = 0;
        switch (msgId) {
            case 0x76:
                // 只有gamma的情况。
                int offset = 0;
                if (sensorData.length == 4) {
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadDose(_fvalue);
                }
                // 有gamma+alphabeta剂量率的情况。
                if (sensorData.length == 8) {
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadDose(_fvalue);
                    offset = 4;
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadBeta(_fvalue);
                }
                // 有gamma+alphabeta剂量率、累计剂量率的情况。
                if (sensorData.length == 17) {
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadDose(_fvalue);
                    offset = 4;
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadBeta(_fvalue);
                    offset = 8;
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadDoseAll(_fvalue);
                    offset = 12;
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                    nuclearMod.setRadBetaAll(_fvalue);
                    // 核检测模式 1：αβ模式 2：γ模式 3：两种模式同时
                    offset = 16;
                    nuclearMod.setNuclearDetectionMode(sensorData[offset]);
                }
                return;
            default:
                return;
        }
    }
}
