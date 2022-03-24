package com.htnova.access.socket;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.htnova.access.dataparser.protocol.ProtocolService;
import com.htnova.accessdroid.SystemSettingStore;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     Socket处理通用服务类：
 *     （1）统一处理心跳。
 *     （2）统一处理发送。
 *     （3）判断设备是否需要通过Socket发送。
 * </pre>
 */
@Slf4j
public class CommonSocketService {
    // 每个SN对应一个缓存，对于同一个SN，如有新的串口连接创建，则需要关闭之前创建的串口连接。
    private static final Map<String, Socket> clientSocketCache = new ConcurrentHashMap<>();

    // 记录每一批次的初始化时间。
    // 线程的每一次执行，需要比较创建时间与当前缓存时间是否一致，如果不一致，则终止线程。
    // 保证相同业务只有一个线程，防止数据冲突。
    private static final Map<String, Long> timeInMillisCache = new ConcurrentHashMap<>();

    private BatchThreadHandler batchThreadHandler = new BatchThreadHandler();

    private int heartBeatInterval = 10000;

    private static CommonSocketService instance;

    private CommonSocketService(){

    }

    public static CommonSocketService getInstance(){
        if(instance == null){
            instance = new CommonSocketService();
        }
        return instance;
    }

    public void initHeartBeat() {
        closeAll();

        Thread newThread = new Thread(() -> {
            Long currTimeInMillis = System.currentTimeMillis();
            addTimeInMillis("initHeartBeat", currTimeInMillis);

            while (true) {
                // 有新的初始化，则退出当前线程。
                if (!containsTimeInMillis("initHeartBeat", currTimeInMillis)) {
                    return;
                }

                try {
                    String deviceType = SystemSettingStore.getDeviceType();
                    String sn = SystemSettingStore.getSn();
                    Socket socket = clientSocketCache.get(sn);

                    // 有些设备如FCBR-100M，需要客户端连接之后发送心跳，才会持续输出数据。
                    if (socket != null) {
                        byte[] crcAndEncodeBytes = ProtocolService.buildReq((byte)0x00, deviceType, sn, null);
                        if (crcAndEncodeBytes != null) {
                            send(deviceType, sn, socket, crcAndEncodeBytes);
                        }
                    }
                } catch (Exception e) {
                    log.error("心跳发送异常", e);
                } finally {
                    try {
                        Thread.sleep(heartBeatInterval);
                    } catch (Exception e) {
                        log.error("心跳发送线程休眠异常", e);
                    }
                }
            }
        });

        try {
            log.info("心跳发送线程开始启动");
            newThread.start();
            batchThreadHandler.add(newThread);
            log.info("心跳发送线程启动完成");
        } catch (Exception e) {
            log.error("心跳发送线程启动异常", e);
        }
    }

    public void addClientSocket(String sn, Socket socket) {
        // 如果有存在，则先关闭，防止多个相同业务逻辑的Socket存在。
        closeClientSocket(sn);

        clientSocketCache.put(sn, socket);
    }

    public boolean containsClientSocket(String sn) {
        return clientSocketCache.containsKey(sn);
    }

    public Socket getClientSocket(String sn) {
        return clientSocketCache.get(sn);
    }

    public void addTimeInMillis(String handlerName, Long timeInMillis) {
        timeInMillisCache.put(handlerName, timeInMillis);
    }

    public boolean containsTimeInMillis(String handlerName, Long currTimeInMillis) {
        if (!timeInMillisCache.containsKey(handlerName)) {
            return false;
        }

        Long lastTimeInMillis = timeInMillisCache.get(handlerName);
        return (currTimeInMillis.longValue() == lastTimeInMillis.longValue());
    }

    public void send(String deviceType, String sn, Socket socket, byte[] sendBytes) {
        try {
            if (socket == null || socket.getOutputStream() == null) {
                log.error("设备{}，Socket为空，数据未发送，直接返回", sn);
                return;
            }

            if (socket.isClosed()) {
                log.error("设备{}，Socket已关闭，数据未发送，直接返回", sn);
                return;
            }

            if (sendBytes == null || sendBytes.length == 0) {
                log.error("设备{}，Socket发送内容为空，直接返回", sn);
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("设备{}，开始发送心跳", sn);
            }

            socket.getOutputStream().write(sendBytes);

            if (log.isDebugEnabled()) {
                log.debug("设备{}，发送心跳完成", sn);
            }
        } catch (Exception e) {
            InetAddress inetAddress = socket.getInetAddress();
            log.error("设备{}，通过{}:{}发送消息异常", sn, inetAddress.getHostAddress(), socket.getPort(), e);
        }
    }

    public void closeAll() {
        batchThreadHandler.stopAll();
        clientSocketCache.forEach((sn, socket) -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {
                log.error("设备{}，关闭ClientSocket连接异常", sn, e);
            }
        });
        clientSocketCache.clear();
        timeInMillisCache.clear();
    }

    public void closeClientSocket(String sn) {
        if (clientSocketCache.containsKey(sn)) {
            Socket socket = clientSocketCache.get(sn);
            if (socket == null || socket.isClosed()) {
                return;
            }

            try {
                socket.close();
            } catch (Exception e) {
                log.error("设备{}，关闭ClientSocket连接异常", sn, e);
            }
            clientSocketCache.remove(sn);
        }
    }

    public void closeClientSocket(String sn, Socket socket) {
        if (socket == null || socket.isClosed()) {
            return;
        }

        try {
            socket.close();
        } catch (Exception e) {
            log.error("设备{}，关闭ClientSocket连接异常", sn, e);
        }
    }

}
