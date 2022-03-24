package com.htnova.access.socket;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchThreadHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private List<Thread> threadList = new ArrayList<>();

    public void add(Thread thread) {
        threadList.add(thread);
    }

    public void stopAll() {
        // 关闭已有数据模拟线程
        threadList.forEach(thread -> {
            try {
                thread.interrupt();
            } catch (Exception e) {
                log.error("中断线程异常", e);
            }
        });
        threadList.clear();
    }
}
