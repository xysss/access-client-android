package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.pojo.dto.ModNuclearDto;

/** Nuclear模块的协议解析实现类。 */
class ModNuclearImpl extends AbstractModProt {
    private static ModNuclearRg500 modNuclearRg500 = new ModNuclearRg500();

    @Override
    void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception {
        ModNuclearDto nuclearMod = (ModNuclearDto)getMod(deviceRawData);
        if (0 == busiType) {
            modNuclearRg500.handleVer1(nuclearMod, sensorData, 0x76);
        }
    }

    @Override
    AbstractModExt getMod(AbstractDevice deviceRawData) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;
        if (deviceDataDto.getNuclearMod() != null) {
            return deviceDataDto.getNuclearMod();
        }
        ModNuclearDto nuclearMod = new ModNuclearDto();
        deviceDataDto.setNuclearMod(nuclearMod);
        return nuclearMod;
    }
}
