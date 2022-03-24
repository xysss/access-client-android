package com.htnova.access.dataparser.protocol;

import java.util.List;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModEcDto;
import com.htnova.access.pojo.dto.ModGd606Dto;
import com.htnova.access.pojo.dto.SensorData;

/** EC模块的协议解析实现类。 */
class ModEcImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        ModEcDto ecMod = (ModEcDto)getMod(deviceRawData);
        handleEcData(deviceRawData, ecMod, sensorData, 0x24);
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getEcMod() != null) {
            return deviceDataDto.getEcMod();
        }
        ModEcDto ecMod = new ModEcDto();
        deviceDataDto.setEcMod(ecMod);
        return ecMod;
    }

    private void handleEcData(AbstractDevice deviceRawData, ModEcDto ecMod, byte[] sensorData, int msgId)
        throws Exception {
        switch (msgId) {
            case 0x24:
                int startPos = 0;
                ModGd606Dto modGd606Dto =
                    Gd606DataResolve.getGd606SensorData(startPos, deviceRawData, sensorData, msgId);
                List<SensorData> sensorDatas = modGd606Dto.getSensors();
                if (sensorDatas != null && sensorDatas.size() > 0) {
                    sensorDatas.forEach(tempData -> {
                        if (tempData.getCode() != null && tempData.getCode().startsWith("O2")) {
                            ecMod.setO2(tempData);
                        }
                        if (tempData.getCode() != null && tempData.getCode().startsWith("NO2")) {
                            ecMod.setNo2(tempData);
                        }
                        if (tempData.getCode() != null && tempData.getCode().startsWith("H2S")) {
                            ecMod.setH2s(tempData);
                        }
                        if (tempData.getCode() != null && tempData.getCode().startsWith("HCN")) {
                            ecMod.setHcn(tempData);
                        }
                        if (tempData.getCode() != null && tempData.getCode().startsWith("COCL2")) {
                            ecMod.setCocl2(tempData);
                        }
                    });

                    // 未解析到传感器数据，以全0代替。
                    if (ecMod.getO2() == null) {
                        log.error("ecMod-O2未解析到，以全0代替");
                        SensorData o2 = new SensorData();
                        ecMod.setO2(o2);
                    }
                    if (ecMod.getNo2() == null) {
                        log.error("ecMod-NO2未解析到，以全0代替");
                        SensorData no2 = new SensorData();
                        ecMod.setNo2(no2);
                    }
                    if (ecMod.getH2s() == null) {
                        log.error("ecMod-H2S未解析到，以全0代替");
                        SensorData h2s = new SensorData();
                        ecMod.setH2s(h2s);
                    }
                    if (ecMod.getHcn() == null) {
                        log.error("ecMod-HCN未解析到，以全0代替");
                        SensorData hcn = new SensorData();
                        ecMod.setHcn(hcn);
                    }
                    if (ecMod.getCocl2() == null) {
                        log.error("ecMod-COCL2未解析到，以全0代替");
                        SensorData cocl2 = new SensorData();
                        ecMod.setCocl2(cocl2);
                    }
                } else {
                    // 没有数据时，以全0代替。
                    log.error("ecMod总体未解析到，以全0代替");
                    SensorData o2 = new SensorData();
                    SensorData no2 = new SensorData();
                    SensorData h2s = new SensorData();
                    SensorData hcn = new SensorData();
                    SensorData cocl2 = new SensorData();
                    ecMod.setO2(o2);
                    ecMod.setNo2(no2);
                    ecMod.setH2s(h2s);
                    ecMod.setHcn(hcn);
                    ecMod.setCocl2(cocl2);
                }
                return;
            default:
                return;
        }
    }
}
