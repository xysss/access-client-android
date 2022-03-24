package com.htnova.access.dataparser.protocol;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceConfig;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModSysDto;
import com.htnova.access.sysconfig.constdef.RunStateDef;

/** SYS模块的协议解析实现类。 */
class ModSysImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        ModSysDto sysMod = (ModSysDto)getMod(deviceRawData);
        if (busiType == 1) {
            analysisFcbr100mData(deviceRawData, sysMod, sensorData, msgId);
        } else if (AbstractModProt.PROT_VER1 == protVer) {
            analysisVer1Data(deviceRawData, sysMod, sensorData, msgId);
        } else if (AbstractModProt.PROT_VER2 == protVer) {
            analysisVer2Data(deviceRawData, sysMod, sensorData, msgId);
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getSysMod() != null) {
            return deviceDataDto.getSysMod();
        }
        ModSysDto sysMod = new ModSysDto();
        deviceDataDto.setSysMod(sysMod);
        return sysMod;
    }

    private void analysisVer1Data(AbstractDevice deviceRawData, ModSysDto sysMod, byte[] sensorData, int msgId)
        throws Exception {
        switch (msgId) {
            case 0x51:
                sysMod.setDoorState(NumberUtil.bytes2Int(sensorData, true));
                if (sysMod.getDoorState() != 0) {
                    sysMod.setRunstate(RunStateDef.MAINTANCE);
                }
                return;
            case 0x61:
                deviceRawData.setSn(Long.toString(NumberUtil.bytes2Long(sensorData, true)));
                return;
            default:
                return;
        }
    }

    private void analysisVer2Data(AbstractDevice deviceRawData, ModSysDto sysMod, byte[] sensorData, int msgId)
        throws Exception {
        switch (msgId) {
            case 0x00:
                deviceRawData.setSn(Long.toString(NumberUtil.bytes2Long(sensorData, true)));
                return;
            case 0x51:
                sysMod.setDoorState(NumberUtil.bytes2Int(sensorData, true));
                if (sysMod.getDoorState() != 0) {
                    sysMod.setRunstate(RunStateDef.MAINTANCE);
                }
                return;
            case 0x61:
                sysMod.setMainTemp(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x62:
                sysMod.setMainHumidity(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x44:
                byte[] tempBytes = new byte[1];
                tempBytes[0] = sensorData[0];
                sysMod.setBioGasHave(NumberUtil.bytes2Int(tempBytes, true));
                tempBytes[0] = sensorData[1];
                sysMod.setBioGasState(NumberUtil.bytes2Int(tempBytes, true));
                return;
            case 0x63:
                sysMod.setLvState(NumberUtil.bytes2Int(sensorData, true));
                return;
            default:
                return;
        }
    }

    private void analysisFcbr100mData(AbstractDevice deviceRawData, ModSysDto sysMod, byte[] sensorData, int msgId)
        throws Exception {
        switch (msgId) {
            case 0x64:
                sysMod.setBatteryChargeState(sensorData[0]);
                sysMod.setBatteryCapacity(sensorData[1]);
                return;
            case 0x65:
                if (sensorData[0] == 0) {
                    sysMod.setMainTemp(sensorData[1]);
                } else {
                    sysMod.setTemp(sensorData[1]);
                }
                return;
            case 0x66:
                if (sensorData[0] == 0) {
                    sysMod.setMainHumidity(sensorData[1]);
                } else {
                    sysMod.setHumidity(sensorData[1]);
                }
                return;
            case 0x67:
                byte[] pumpStates = NumberUtil.getBits(sensorData[0], false);
                sysMod.setPumpInstallState(pumpStates[0]);
                sysMod.setPumpRunstate(pumpStates[1]);
                // 故障：处于运行。
                if (pumpStates[2] == 1) {
                    sysMod.setPumpRunstate((byte)2);
                }
                sysMod.setPumpFlowRate(NumberUtil.bytes2Int(sensorData, 1, 2, false));
                return;
            case 0x68:
                // 设备端日期格式为BCD码，使用时需要转换。
                // 年份只取后面两位，忽略前面两位，因此需要根据当前的时间补齐。
                int yearBcd = sensorData[0];
                int monthBcd = sensorData[1];
                int dateBcd = sensorData[2];
                int hourBcd = sensorData[3];
                int minuteBcd = sensorData[4];
                int secondBcd = sensorData[5];

                LocalDateTime currLocalDateTime = LocalDateTime.now();
                int currYearAditional = currLocalDateTime.getYear() / 100 * 100;
                int year = currYearAditional + NumberUtil.bcd2Byte((byte)yearBcd);
                int month = NumberUtil.bcd2Byte((byte)monthBcd);
                int date = NumberUtil.bcd2Byte((byte)dateBcd);
                int hour = NumberUtil.bcd2Byte((byte)hourBcd);
                int minute = NumberUtil.bcd2Byte((byte)minuteBcd);
                int second = NumberUtil.bcd2Byte((byte)secondBcd);

                Long dataDateTime = null;
                try {
                    if (month == 0 && date == 0) {
                        dataDateTime = currLocalDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
                    } else {
                        LocalDateTime localDateTime = LocalDateTime.of(year, month, date, hour, minute, second);
                        dataDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
                    }
                } catch (Exception e) {
                    log.error("设备{}获取日期异常", deviceRawData.getSn(), e);
                    dataDateTime = System.currentTimeMillis();
                }
                sysMod.setDataDateTime(dataDateTime);

                // 系统时间没有单独的协议来处理，是从数据中直接获取到，只要获取到就更新到配置中。
                DeviceConfig deviceConfig =
                    HisDeviceConfig.getConfig(deviceRawData.getDeviceType(), deviceRawData.getSn());
                if (deviceConfig != null) {
                    deviceConfig.setDeviceTime(dataDateTime);
                }

                return;
            case 0x69:
                sysMod.setTotalMemSize(NumberUtil.bytes2Int(sensorData, 0, 2, false));
                sysMod.setFreeMemSize(NumberUtil.bytes2Int(sensorData, 2, 2, false));
                return;
            case 0x6A:
                // 获取有多少个故障。
                int faultCount = NumberUtil.byte2Int(sensorData[0]);
                if (faultCount > 0) {
                    Map<String, String> faults = new HashMap<>();
                    for (int i = 0; i < faultCount; i++) {
                        String faultCode = String.valueOf(NumberUtil.byte2Int(sensorData[i + 1]));
                        faults.put(faultCode, getFaultName(faultCode));
                    }
                    sysMod.setFaults(faults);
                }
                return;
            case (byte)0x81:
                sysMod.setMobileEnableState(sensorData[0]);
                sysMod.setMobileSignalStrength(sensorData[1]);
                sysMod.setMobileWorkingState(sensorData[2]);
                return;
            default:
                return;
        }
    }

    private static String getFaultName(String faultCode) {
        // 0x01
        if ("1".equals(faultCode)) {
            return "检测单元1通信故障";
        }

        // 0x02
        if ("2".equals(faultCode)) {
            return "检测单元2通信故障";
        }

        // 0x03
        if ("3".equals(faultCode)) {
            return "CWA通信故障";
        }

        // 其它未编号的故障代码。
        return "未知故障";
    }
}
