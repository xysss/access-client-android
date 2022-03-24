package com.htnova.access.datahandle.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.datahandle.pojo.ResultDto;
import com.htnova.access.datahandle.service.ParserService;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.accessdroid.EventPublisher;

/**
 * <pre>
 *     数据入口基类。提供了数据入口处理的基本流程和扩展点：
 *     （1）首先调用parser的parseRawData，解析协议，获取原始数据。
 *     （2）然后提供afterParseRawData的扩展点，可以对数据做进一步处理。
 *     （3）然后再调用parser的parseDeviceData，处理报警。
 *     （4）然后提供afterParseDeviceData的扩展点，可以对数据做进一步处理。
 *     （5）除了传感器实时数据之外，还提供了其它数据的扩展点handleEntryOther。
 * </pre>
 */
public class EntryBasicServiceImpl {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 通用设备入口数据处理的切入点，数据包为一帧原始数据。
     *
     * @param dataParseService
     *            用以数据解析的服务接口实现。
     * @param entryServiceName
     *            设备入口服务名称，用以记录日志。
     * @param dataFrame
     *            设备原始数据包，一帧原始数据。
     * @param productCode
     *            产品代码，如0x08。
     * @param deviceType
     *            设备类型，如BM3001。
     * @param sn
     *            设备序列号。
     * @param deviceSeq
     *            有些设备由多台数据组合而成，如HT308PRD/CIAE-RMS-C/C2S/2006060001/01/。
     * @param isPubData
     *            是否发布设备协议解析数据。
     * @return BaseInfoVO类型的数据。
     */
    protected ResultDto handleEntryData(ParserService dataParseService, String entryServiceName, byte[] dataFrame,
                                        int productCode, String deviceType, String sn, String deviceSeq, boolean isPubData) {
        // 协议内容（或服务接口）解析。
        ResultDto resultDto = dataParseService.parseRawData(dataFrame, productCode, deviceType, sn);

        if (null == resultDto || resultDto.getData() == null) {
            log.error("{}原始数据解析失败，返回空对象：[deviceType={}]", entryServiceName, deviceType);
            return null;
        }

        // 以下对解析后的结果进一步分析。
        if (AbstractDevice.DATA_TYPE_DATA == resultDto.getCode()) {
            DeviceDataDto deviceDataDto = (DeviceDataDto)resultDto.getData();

            // 原始数据解析后做的一些特殊处理逻辑：如FCBR-100G中伽马相机的数据合并，判断是否需要模拟数据。这些都与协议解析后的原始数据相关。
            afterParseRawData(deviceDataDto, deviceDataDto.getSn(), deviceSeq);

            // 对解析后的数据做进一步分析（如报警分析，设置报警状态等）。
            DeviceDataDto parseDeviceResult = dataParseService.parseDeviceData(deviceDataDto);
            if (parseDeviceResult != null) {
                // 数据进一步分析后做的一些特殊处理逻辑。
                afterParseDeviceData(deviceDataDto);

                // 设置要返回的数据对象。
                resultDto.setData(deviceDataDto);

                // 发布数据，供Android端界面显示以及报警，故障保存用。
                EventPublisher.getInstance().pubDataRecvEvent(deviceDataDto);

                return resultDto;
            }

            return null;
        }

        return null;
    }

    protected void afterParseRawData(DeviceDataDto deviceDataDto, String sn, String deviceSeq) {}

    protected void afterParseDeviceData(DeviceDataDto deviceDataDto) {}
}
