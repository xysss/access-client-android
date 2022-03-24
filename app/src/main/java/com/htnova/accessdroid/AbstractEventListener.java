package com.htnova.accessdroid;

import java.util.EventListener;
import java.util.EventObject;

/**
 * <pre>
 *     抽象事件发布类，用于后台与Android的UI进行数据交互。
 * </pre>
 */
abstract public class AbstractEventListener implements EventListener {
    /**
     * 出来事件，如接收数据，分析后更新UI等操作。
     * @param event 传递的事件对象。
     */
    abstract public void handleEvent(EventObject event);

    /**
     * 判断是否是当前需要处理的事件。
     * @param event 传递的事件对象。
     * @return 需要处理返回true，不需要处理返回false。
     */
    abstract public boolean isMyEvent(EventObject event);
}
