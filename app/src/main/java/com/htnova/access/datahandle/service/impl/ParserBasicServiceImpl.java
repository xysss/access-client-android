package com.htnova.access.datahandle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.datahandle.pojo.ResultDto;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.sysconfig.constdef.ProductDef;

/**
 * <pre>
 *     数据解析基类。提供了数据解析处理的基本流程和扩展点：
 *     （1）提供了parseRawData，解析协议，获取原始数据。会调用getProtocolData和parseOtherData。
 *     （2）提供了parseDeviceData，处理报警。
 *     （3）以上方法只供相应的数据入口entry服务调用。
 *     除了parseOtherData之外，对数据额外处理的扩展点都放到entry服务中，不再提供扩展点。
 * </pre>
 */
public abstract class ParserBasicServiceImpl {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected AbstractDevice getProtocolData(byte[] dataFrame, int productCode, String deviceType, String sn)
        throws Exception {
        return null;
    }

    /**
     * 原始设备数据解析，将原始设备数据转为设备对象数据，方便处理。 该数据直接来自与协议或通过服务直接推送过来。
     *
     * @param dataFrame
     *            协议数据帧。
     * @param productCode
     *            产品代码，如0x05、0x08。
     * @param deviceType
     *            设备类型，如FCBR-100、BM3001。
     * @param sn
     *            设备号，如2004020039。
     * @return 解析后的BaseInfoVO对象。
     */
    protected ResultDto parseRawData(String parseServiceName, byte[] dataFrame, int productCode, String deviceType,
                                     String sn) {
        AbstractDevice deviceRawData = null;
        try {
            deviceRawData = getProtocolData(dataFrame, productCode, deviceType, sn);
            deviceRawData.setProductCode(productCode);
            deviceRawData.setRunMode(ProductDef.RUN_MODE_MONITOR);
            if (null != sn) {
                deviceRawData.setSn(sn);
            }
        } catch (Exception e) {
            log.error("{}协议数据解析异常：[deviceType={}]", parseServiceName, deviceType, e);
            return null;
        }

        // 协议转为对象。
        ResultDto resultDto = wrapperRawDataWithResult(deviceRawData);
        if (null == resultDto) {
            log.error("{}协议转对象为空：[deviceType={}]", parseServiceName, deviceType);
            return null;
        }
        return resultDto;
    }

    /**
     * 308v2进一步设备数据解析，产生报警信息、前端要展示的结构等进一步的内容。
     *
     * @param deviceData
     *            协议解析后的设备对象数据。
     * @return 进一步解析后的设备对象数据。
     */
    public DeviceDataDto parseDeviceData(AbstractDevice deviceData, boolean isHandleAlarm) {
        DeviceDataDto deviceDataDto = (DeviceDataDto)deviceData;
        return deviceDataDto;
    }

    protected ResultDto parseOtherData(AbstractDevice deviceRawData) {
        return null;
    }

    /**
     * 对协议解析后的Map数据进行分析。 code含义：1-心跳，3-版本号，65-数据内容，其余5-77位命令相应。
     *
     * @param deviceRawData
     *            协议解析后的数据对象。
     * @return BaseInfoVO对象。
     */
    private ResultDto wrapperRawDataWithResult(AbstractDevice deviceRawData) {
        if (null == deviceRawData) {
            return null;
        }

        int rawDataTypeValue = deviceRawData.getDataType();

        // 普通数据。
        if (AbstractDevice.DATA_TYPE_DATA == rawDataTypeValue) {
            ResultDto<DeviceDataDto> resultDto = new ResultDto<>();
            resultDto.setCode(rawDataTypeValue);

            // 设备的基本抽象属性。
            DeviceDataDto deviceDataDto = (DeviceDataDto)deviceRawData;

            // 设置BaseInfoVO的内容并返回。
            resultDto.setData(deviceDataDto);
            return resultDto;
        }

        return parseOtherData(deviceRawData);
    }
}
