package com.htnova.access.dataparser.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.pojo.AbstractModExt;

/**
 * 单个传感器数据协议解析的抽象父类，定义了协议解析的抽象接口。
 */
abstract class AbstractModProt {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public static final int PROT_VER1 = 1;
    public static final int PROT_VER2 = 2;

    /**
     * <pre>
     *     传感器解析的抽象方法，不同传感器实现该方法，将解析后的数据填充到deviceRawData设备数据实体中。
     * </pre>
     * 
     * @param deviceRawData
     *            设备数据实体。
     * @param sensorData
     *            传感器数据。
     * @param msgId
     *            消息号。
     * @param protVer
     *            协议版本，根据业务情况设定，记录协议的版本情况。
     * @param busiType
     *            业务类型，业务情况设定，可以区分不同的业务。
     * @throws Exception
     */
    abstract void protAnalysis(AbstractDevice deviceRawData, byte[] sensorData, int msgId, int protVer, int busiType)
        throws Exception;

    /**
     * 返回实际的传感器数据实体。
     * 
     * @param deviceRawData
     *            设备数据实体。
     * @return 当前传感器数据实体。
     */
    abstract AbstractModExt getMod(AbstractDevice deviceRawData);
}
