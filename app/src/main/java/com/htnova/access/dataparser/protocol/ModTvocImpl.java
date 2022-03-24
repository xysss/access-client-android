package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModTvocDto;
import com.htnova.access.pojo.dto.SensorData;

/** TVOC模块的协议解析实现类。 */
class ModTvocImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        String deviceType = deviceRawData.getDeviceType();
        String sn = deviceRawData.getSn();
        ModTvocDto tvocMod = (ModTvocDto)getMod(deviceRawData);
        switch (busiType) {
            case 0:
                handleVer1(tvocMod, sensorData, 0x21);
                break;
            case 1:
                handleVp100(tvocMod, sensorData, 0x22);
                break;
            case 3:
                handleFcbr100m(tvocMod, sensorData, 0x26, deviceType, sn);
                break;
            default:
                break;
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getTvocMod() != null) {
            return deviceDataDto.getTvocMod();
        }
        ModTvocDto tvocMod = new ModTvocDto();
        deviceDataDto.setTvocMod(tvocMod);
        return tvocMod;
    }

    private void handleVer1(ModTvocDto tvocMod, byte[] sensorData, int msgId) throws Exception {
        double _dvalue;
        switch (msgId) {
            case 0x21:
                int offset = 0;
                _dvalue = NumberUtil.bytes2Int(sensorData, offset, 2, true) / 1.0;
                _dvalue = NumberUtil.getDoublePrecise(_dvalue, 2);
                tvocMod.setNh3(_dvalue);

                offset = 2;
                _dvalue = NumberUtil.bytes2Int(sensorData, offset, 2, true) / 1.0;
                _dvalue = NumberUtil.getDoublePrecise(_dvalue, 2);
                tvocMod.setCo(_dvalue);

                // 传感器取的cl2值过大，选择除以10用于降低值。
                offset = 4;
                _dvalue = NumberUtil.bytes2Int(sensorData, offset, 2, true) / 10.0;
                _dvalue = NumberUtil.getDoublePrecise(_dvalue, 2);
                tvocMod.setCl2(_dvalue);

                offset = 6;
                tvocMod.setPidValue(NumberUtil.bytes2Int(sensorData, offset, 2, true));
                return;
            default:
                return;
        }
    }

    private void handleFcbr100m(ModTvocDto tvocMod, byte[] sensorData, int msgId, String deviceType, String sn)
        throws Exception {
        switch (msgId) {
            case 0x26:
                SensorData tempSensorData = Fcbr100mDataResolve.getFcbr100mSensorData(sensorData, 1, deviceType, sn);
                if (tempSensorData != null) {
                    tvocMod.addSensor(tempSensorData);
                }

                return;
            default:
                return;
        }
    }

    private void handleVp100(ModTvocDto tvocMod, byte[] sensorData, int msgId) throws Exception {
        float _fvalue;
        switch (msgId) {
            case 0x22:
                if (sensorData != null && sensorData.length == 5) {
                    int offset = 0;
                    _fvalue = NumberUtil.bytes2Int(sensorData, offset, 4, true) / 100.0F;
                    _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
                    tvocMod.setPidValue(_fvalue);

                    offset = 4;
                    int _ivalue = sensorData[offset];
                    if (_ivalue == 1) {
                        tvocMod.setModAlarmState(true);
                    }
                }

                return;
            default:
                return;
        }
    }
}
