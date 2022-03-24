package com.htnova.access.dataparser.protocol;

import java.nio.charset.StandardCharsets;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModCwaDto;
import com.htnova.access.pojo.dto.SensorData;
import com.htnova.access.sysconfig.constdef.GasDef;
import com.htnova.access.sysconfig.constdef.RunStateDef;

/** CWA模块的协议解析实现类。 */
class ModCwaImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();
        ModCwaDto cwaMod = (ModCwaDto)getMod(deviceRawData);
        if (1 == busiType) {
            handleRwaHt(deviceRawData.getSn(), cwaMod, sensorData);
        } else if (2 == busiType) {
            handleFcbr100m(cwaMod, sensorData, msgId, deviceType, sn);
        } else {
            if (AbstractModProt.PROT_VER1 == protVer) {
                handleVer1(cwaMod, sensorData, msgId);
            } else if (AbstractModProt.PROT_VER2 == protVer) {
                handleVer2(cwaMod, sensorData, msgId);
            }
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getCwaMod() != null) {
            return deviceDataDto.getCwaMod();
        }
        ModCwaDto cwaMod = new ModCwaDto();
        deviceDataDto.setCwaMod(cwaMod);
        return cwaMod;
    }

    private void handleVer1(ModCwaDto cwaMod, byte[] sensorData, int msgId) throws Exception {
        float _fvalue;
        switch (msgId) {
            case 0x01:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setTemp(_fvalue);
                return;
            case 0x02:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setHumidity(_fvalue);
                return;
            case 0x03:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 6);
                cwaMod.setSccell1RData(_fvalue);
                return;
            case 0x04:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 6);
                cwaMod.setSccell1Data(_fvalue);
                return;
            case 0x05:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 6);
                cwaMod.setSccell2RData(_fvalue);
                return;
            case 0x06:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 6);
                cwaMod.setSccell2Data(_fvalue);
                return;
            case 0x07:
                cwaMod.setConcentration(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x08:
                String name = new String(sensorData, StandardCharsets.UTF_8);
                cwaMod.setGasName(gasName(name));
                return;
            case 0x09:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setFlow(_fvalue);
                return;
            case 0x0A:
                cwaMod.setHtFault(NumberUtil.bytes2Int(sensorData, true));
                if (cwaMod.getHtFault() != 0) {
                    cwaMod.setRunstate(RunStateDef.FAULT);
                }
                return;
            case 0x0B:
                cwaMod.setHtAlarm(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x0C:
                int[] ims = new int[16];
                byte[] tempBytes = new byte[2];
                for (int i = 0; i < sensorData.length; i += 2) {
                    tempBytes[0] = sensorData[i];
                    tempBytes[1] = sensorData[i + 1];
                    ims[i / 2] = NumberUtil.bytes2Int(tempBytes, true);
                }
                cwaMod.setIms(ims);
                return;
            case 0x0D:
                cwaMod.setHtState(NumberUtil.bytes2Int(sensorData, true));
                return;
            default:
                return;
        }
    }

    private void handleVer2(ModCwaDto cwaMod, byte[] sensorData, int msgId) throws Exception {
        float _fvalue;
        switch (msgId) {
            case 0x01:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setTemp(_fvalue);
                return;
            case 0x02:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setHumidity(_fvalue);
                return;
            case 0x03:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                cwaMod.setSccell1Data(_fvalue);
                return;
            case 0x04:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                cwaMod.setSccell1RData(_fvalue);
                return;
            case 0x05:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                cwaMod.setSccell2Data(_fvalue);
                return;
            case 0x06:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
                cwaMod.setSccell2RData(_fvalue);
                return;
            case 0x07:
                cwaMod.setConcentration(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x08:
                _fvalue = NumberUtil.bytes2Float(sensorData, true);
                _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                cwaMod.setFlow(_fvalue);
                return;
            case 0x09:
                String name = new String(sensorData, StandardCharsets.UTF_8);
                cwaMod.setGasName(gasName(name));
                return;
            case 0x0A:
                cwaMod.setHtFault(NumberUtil.bytes2Int(sensorData, true));
                if (cwaMod.getHtFault() != 0) {
                    cwaMod.setRunstate(RunStateDef.FAULT);
                }
                return;
            case 0x0B:
                cwaMod.setHtAlarm(NumberUtil.bytes2Int(sensorData, true));
                return;
            case 0x0C:
                int[] ims = new int[16];
                byte[] tempBytes = new byte[2];
                for (int i = 0; i < sensorData.length; i += 2) {
                    tempBytes[0] = sensorData[i];
                    tempBytes[1] = sensorData[i + 1];
                    ims[i / 2] = NumberUtil.bytes2Int(tempBytes, true);
                }
                cwaMod.setIms(ims);
                return;
            case 0x0D:
                cwaMod.setHtState(NumberUtil.bytes2Int(sensorData, true));
                return;
            default:
                return;
        }
    }

    private void handleFcbr100m(ModCwaDto cwaMod, byte[] sensorData, int msgId, String deviceType, String sn)
        throws Exception {
        switch (msgId) {
            case (byte)0x91:
                SensorData tempSensorData = Fcbr100mDataResolve.getFcbr100mSensorData(sensorData, 2, deviceType, sn);
                if (tempSensorData != null) {
                    cwaMod.addSensor(tempSensorData);
                }

                return;
            default:
                return;
        }
    }

    private void handleRwaHt(String sn, ModCwaDto cwaMod, byte[] sensorData) throws Exception {
        if (sensorData.length != 138) {
            log.error("sn={}的ht数据长度不正确", sn);
            throw new Exception("ht数据长度不正确");
        }

        // 25-56
        int offset = 24;
        String name = new String(subBytes(sensorData, 24, 32), StandardCharsets.UTF_8);
        cwaMod.setGasName(gasName(name));

        // 57-60
        offset = 56;
        cwaMod.setConcentration(NumberUtil.bytes2Int(sensorData, offset, 4, true));

        // 69-100
        byte[] imsData = subBytes(sensorData, 68, 32);
        int[] ims = new int[16];
        byte[] tempBytes = new byte[2];
        for (int i = 0; i < imsData.length; i += 2) {
            tempBytes[0] = imsData[i];
            tempBytes[1] = imsData[i + 1];
            ims[i / 2] = NumberUtil.bytes2Int(tempBytes, true);
        }
        cwaMod.setIms(ims);

        // 101-104
        offset = 100;
        float _fvalue;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
        cwaMod.setSccell1RData(_fvalue);

        // 105-108
        offset = 104;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
        cwaMod.setSccell1Data(_fvalue);

        // 109-112
        offset = 108;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
        cwaMod.setTemp(_fvalue);

        // 117-120
        offset = 116;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
        cwaMod.setFlow(_fvalue);

        // 121-124
        offset = 120;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
        cwaMod.setHumidity(_fvalue);

        // 125-126
        offset = 124;
        cwaMod.setHtState(NumberUtil.bytes2Int(sensorData, offset, 2, true));

        // 128
        offset = 127;
        cwaMod.setHtAlarm(NumberUtil.bytes2Int(sensorData, offset, 1, true));

        // 129-132
        offset = 128;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
        cwaMod.setSccell2RData(_fvalue);

        // 133-136
        offset = 132;
        _fvalue = NumberUtil.bytes2Float(sensorData, offset, true);
        _fvalue = NumberUtil.getFloatPrecise(_fvalue, 4);
        cwaMod.setSccell2Data(_fvalue);
    }

    private byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    private String gasName(String strTypeName) {
        if (strTypeName.contains(GasDef.GASTYPE_NERVE[0])) {
            return GasDef.GASTYPE_NERVE[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_BLISTER[0])) {
            return GasDef.GASTYPE_BLISTER[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_BLOOD[0])) {
            return GasDef.GASTYPE_BLOOD[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_CHOKING[0])) {
            return GasDef.GASTYPE_CHOKING[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_INCAPACITATING[0])) {
            return GasDef.GASTYPE_INCAPACITATING[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_TIC[0])) {
            return GasDef.GASTYPE_TIC[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_CHEMICAL[0])) {
            return GasDef.GASTYPE_CHEMICAL[1];
        }
        if (strTypeName.contains(GasDef.GASTYPE_TOXIC[0])) {
            return GasDef.GASTYPE_TOXIC[1];
        }
        return GasDef.GASTYPE_AIR[1];
    }
}
