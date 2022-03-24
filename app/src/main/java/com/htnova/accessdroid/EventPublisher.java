package com.htnova.accessdroid;

import com.htnova.access.pojo.dto.DeviceDataDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件发布类。作为生产者与消费者之间的桥梁。
 */
public class EventPublisher {
    private Map<String, AbstractEventListener> listeners = new ConcurrentHashMap<>();

    private static EventPublisher instance;

    private EventPublisher(){
    }

    public static EventPublisher getInstance(){
        if(instance == null){
            instance = new EventPublisher();
        }
        return instance;
    }

    public void addListener(AbstractEventListener listener) {
        if(!listeners.containsKey(listener.getClass().getName())){
            listeners.put(listener.getClass().getName(), listener);
        }
    }

    public void pubDataRecvEvent(DeviceDataDto deviceDataDto) {
        DataRecvEvent dataRecvEvent = new DataRecvEvent(this, deviceDataDto);
        listeners.forEach((listenerKey, listener) ->{
            if(listener.isMyEvent(dataRecvEvent)){
                listener.handleEvent(dataRecvEvent);
            }
        });
    }
}