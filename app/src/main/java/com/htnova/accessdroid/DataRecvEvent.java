package com.htnova.accessdroid;

import com.htnova.access.pojo.dto.DeviceDataDto;

import java.util.EventObject;

/**
 * <pre>
 *     数据接收事件，保存接收到的数据。
 * </pre>
 */
public class DataRecvEvent extends EventObject {
    private DeviceDataDto dataInfo;

    /**
     * 构造方法中将事件源和对应的数据初始化到事件对象中。
     *
     * @param source 事件源，即发布事件的对象，当前都是EventPublisher。
     * @param dataInfo 事件数据，事件需要传递的数据实体。
     */
    public DataRecvEvent(Object source, DeviceDataDto dataInfo) {
        super(source);
        this.dataInfo = dataInfo;
    }

    public DeviceDataDto getDataInfo() {
        return dataInfo;
    }
}
