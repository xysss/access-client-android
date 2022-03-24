package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModSmokeDto;
import com.htnova.access.sysconfig.constdef.RunStateDef;

/** Smoke模块的协议解析实现类。 */
class ModSmokeImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        ModSmokeDto smokeMod = (ModSmokeDto)getMod(deviceRawData);
        boolean isBigEndian = (busiType == 0);
        handleProt(smokeMod, sensorData, msgId, isBigEndian);
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getSmokeMod() != null) {
            return deviceDataDto.getSmokeMod();
        }
        ModSmokeDto smokeMod = new ModSmokeDto();
        deviceDataDto.setSmokeMod(smokeMod);
        return smokeMod;
    }

    private void handleProt(ModSmokeDto smokeMod, byte[] sensorData, int msgId, boolean isBigEndian) throws Exception {
        switch (msgId) {
            case 0x31:
                smokeMod.setSmokeData(NumberUtil.bytes2Int(sensorData, isBigEndian));
                return;
            case 0x32:
                smokeMod.setSmokeFire1(NumberUtil.bytes2Int(sensorData, isBigEndian));
                return;
            case 0x33:
                smokeMod.setSmokeFire2(NumberUtil.bytes2Int(sensorData, isBigEndian));
                return;
            case 0x34:
                smokeMod.setSmokeAlarm(NumberUtil.bytes2Int(sensorData, isBigEndian));
                return;
            case 0x35:
                smokeMod.setSmokeInspect(NumberUtil.bytes2Int(sensorData, isBigEndian));
                return;
            case 0x36:
                int smokeFault = NumberUtil.bytes2Int(sensorData, isBigEndian);
                // 协议数据中，1-正常，0-异常。此处进行转换，转换为0-正常，1-异常。
                smokeFault = ((smokeFault == 1) ? 0 : 1);
                smokeMod.setSmokeFault(smokeFault);
                if (smokeMod.getSmokeFault() != 0) {
                    smokeMod.setRunstate(RunStateDef.FAULT);
                }
                return;
            default:
                return;
        }
    }
}
