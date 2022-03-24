package com.htnova.access.datahandle.service.impl308;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.datahandle.pojo.ResultDto;
import com.htnova.access.datahandle.service.ParserService;
import com.htnova.access.datahandle.service.impl.ParserBasicServiceImpl;
import com.htnova.access.dataparser.protocol.ProtocolService;
import com.htnova.access.pojo.dto.DeviceDataDto;

/** 308v2数据解析实现。 */
public class Parser308ServiceImpl extends ParserBasicServiceImpl implements ParserService {
    /**
     * 308v2原始设备数据解析，将原始设备数据转为设备对象数据，方便处理。 该数据直接来自与协议或通过服务直接推送过来。
     *
     * @param dataFrame
     *            协议数据帧。
     * @param productCode
     *            产品代码，如0x05。
     * @param deviceType
     *            设备类型，如FCBR-100、FCBR-100B、FCBR-100PRO。
     * @param sn
     *            设备号，如2004020039。
     * @return 解析后的BaseInfoVO对象。
     */
    @Override
    public ResultDto parseRawData(byte[] dataFrame, int productCode, String deviceType, String sn) {
        return parseRawData("Parser308v2", dataFrame, productCode, deviceType, sn);
    }

    /**
     * 308v2进一步设备数据解析，产生报警信息、前端要展示的结构等进一步的内容。
     *
     * @param deviceData
     *            协议解析后的设备对象数据。
     * @return 进一步解析后的设备对象数据。
     */
    @Override
    public DeviceDataDto parseDeviceData(AbstractDevice deviceData) {
        return parseDeviceData(deviceData, true);
    }

    @Override
    protected AbstractDevice getProtocolData(byte[] dataFrame, int productCode, String deviceType, String sn)
        throws Exception {
        return ProtocolService.getProtocolData(dataFrame, productCode, deviceType, sn);
    }
}
