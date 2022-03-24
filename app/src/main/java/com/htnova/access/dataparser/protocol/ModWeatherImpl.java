package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModWeatherDto;

/** Weather模块的协议解析实现类。 */
class ModWeatherImpl extends AbstractModProt {
    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        ModWeatherDto weatherMod = (ModWeatherDto)getMod(deviceRawData);
        if (msgId == 0x71) {
            int offset = 0;
            float _fvalue = NumberUtil.bytes2Int(sensorData, offset, 2, true) / 100.0F;
            _fvalue = NumberUtil.getFloatPrecise(_fvalue, 2);
            weatherMod.setWindSpeed(_fvalue);

            offset = 2;
            weatherMod.setWindDirection(NumberUtil.bytes2Int(sensorData, offset, 2, true));

            offset = 4;
            weatherMod.setWindScale(NumberUtil.bytes2Int(sensorData, offset, 2, true));
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getWeatherMod() != null) {
            return deviceDataDto.getWeatherMod();
        }
        ModWeatherDto weatherMod = new ModWeatherDto();
        deviceDataDto.setWeatherMod(weatherMod);
        return weatherMod;
    }
}
