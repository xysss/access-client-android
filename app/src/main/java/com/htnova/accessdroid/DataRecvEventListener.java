package com.htnova.accessdroid;

import android.os.Message;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.pojo.dto.DeviceDataDto;

import java.util.EventObject;

/**
 * <pre>
 *     数据接收监听，在收到Socket数据，对分析处理之后的数据实体进行监听，以进行下一步操作。
 * </pre>
 */
public class DataRecvEventListener extends AbstractEventListener {
    public void handleEvent(EventObject event) {
        if(isMyEvent(event)){
            DeviceDataDto deviceDataDto = ((DataRecvEvent)event).getDataInfo();

            // 收到数据之后，通过消息发送给Android主UI线程，以便显示数据。
            Message message = new Message();
            message.what = AbstractDevice.DATA_TYPE_DATA;
            message.obj = deviceDataDto;
            SystemSettingStore.getHandler().sendMessage(message);
        }
    }

    public boolean isMyEvent(EventObject event){
        return  (event instanceof  DataRecvEvent);
    }
}
