package com.htnova.access.datahandle.service;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.datahandle.pojo.ResultDto;
import com.htnova.access.pojo.dto.DeviceDataDto;

/**
 * <pre>
 *     数据解析接口：数据解析一般分两步：
 *     第一步【基本不涉及业务】：解析原始设备数据，生成设备对象数据，仅仅在协议结果的基础上封装对象数据，方便操作。 
 *     第二步【与业务紧密相关】：解析设备对象数据，根据业务要求，对设备对象数据进一步处理，如报警处理。
 * </pre>
 */
public interface ParserService {
    /**
     * 解析原始设备数据，将原始设备数据根据协议（或服务接口）约定转为设备对象数据，方便处理。 该数据直接来自设备或服务。
     *
     * @param dataFrame
     *            协议数据帧。
     * @param productCode
     *            产品代码，如0x05、0x08。
     * @param deviceType
     *            设备类型，如FCBR-100、BM3001、ChemPro100。
     * @param sn
     *            设备号，如2004020039。
     * @return 解析后的BaseInfoVO对象。
     */
    ResultDto parseRawData(byte[] dataFrame, int productCode, String deviceType, String sn);

    /**
     * 进一步解析设备数据，产生报警信息、前端要展示的结构等进一步的内容。
     *
     * @param deviceData
     *            协议解析后的设备对象数据。
     * @return 进一步解析后的设备对象数据。
     */
    DeviceDataDto parseDeviceData(AbstractDevice deviceData);
}
