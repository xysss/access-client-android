package com.htnova.access.dataparser.protocol;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;

/** 308协议解析实现，根据传递的字节数组，获取解析后的数据对象。 */
class Device308Impl extends AbstractDeviceProt {
    private static final byte frameStart308v2 = 0x55;
    private static final byte frameStart308v1 = 0x40;

    private static final AbstractDeviceProt protocol308v1 = new Device308v1Impl();
    private static final AbstractDeviceProt protocol308v2 = new Device308v2Impl();

    @Override
    protected AbstractDevice analysisProtocol(byte[] deviceData, int productCode, String deviceType, String sn)
        throws Exception {
        byte startByte = deviceData[0];
        if (frameStart308v2 == startByte) {
            return protocol308v2.analysisProtocol(deviceData, productCode, deviceType, sn);
        }

        if (frameStart308v1 == startByte) {
            return protocol308v1.analysisProtocol(deviceData, productCode, deviceType, sn);
        }

        log.error("数据校验不通过，deviceType={}，sn={}，bytes={}", deviceType, sn, NumberUtil.bytesToHexString(deviceData));
        throw new Exception("数据验证不通过");
    }
}
