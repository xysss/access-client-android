package com.htnova.access.datahandle.service;

import com.htnova.access.datahandle.pojo.ResultDto;

/**
 * 数据入口接口：定义所有数据入口需要实现的接口方法列表。
 */
public interface EntryService {
    /**
     * 所有入口数据处理的切入点，包括来自MQTT、HttpPost、Socket的数据。
     * 每种设备可由deviceType唯一确定，但因历史原因，存在多个设备采用相同的topic的情况，这些设备productCode相同，解析协议相同，因此，传递了productCode参数。
     * 由于旧版配置无法解析出sn，所以sn可能为空。 某些设备由多台数据组成，所以有deviceSeq，如01、02，这些值具体含义，由设备类型确定是左右还是上下。
     *
     * @param dataPackage
     *            数据包，可能包含多个数据帧。
     * @param productCode
     *            产品代码，如0x05、0x08。
     * @param deviceType
     *            设备类型，如FCBR-100、BM3001、ChemPro100。
     * @param sn
     *            消息队列，旧版配置无法解析出设备号，如HT308PRD/308/C2S/，新版配置可以解析出设备号，如HT308PRD/ht/C2S/2005010001/。
     * @param deviceSeq
     *            有些设备由多台数据组合而成，如HT308PRD/CIAE-RMS-C/C2S/2006060001/01/。
     * @return BaseInfoVO类型的数据。
     */
    ResultDto handleEntryData(byte[] dataPackage, int productCode, String deviceType, String sn, String deviceSeq);
}
