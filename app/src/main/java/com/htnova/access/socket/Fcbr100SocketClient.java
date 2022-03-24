package com.htnova.access.socket;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import com.htnova.access.datahandle.service.EntryService;
import com.htnova.access.datahandle.service.impl308.Entry308ServiceImpl;
import com.htnova.access.dataparser.protocol.ProtocolService;
import com.htnova.access.pojo.po.DeviceEntryConf;
import com.htnova.accessdroid.SystemSettingStore;
import com.htnova.access.sysconfig.constdef.DeviceEntryDef;
import com.htnova.access.sysconfig.constdef.ProductDef;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     Socket接入客户端，通过接入配置中，为TCP类型创建单独的线程处理。
 * </pre>
 */
@Slf4j
public class Fcbr100SocketClient {
    private BatchThreadHandler batchThreadHandler = new BatchThreadHandler();

    private CommonSocketService commonSocketService = CommonSocketService.getInstance();

    private EntryService entryService = new Entry308ServiceImpl();

    private static Fcbr100SocketClient fcbr100SocketClient;

    private Fcbr100SocketClient(){

    }

    public static Fcbr100SocketClient getInstance(){
        if(fcbr100SocketClient == null){
            fcbr100SocketClient = new Fcbr100SocketClient();
        }
        return fcbr100SocketClient;
    }

    public static void main(String[] args) {
        // 联调用的测试验证代码。
        DeviceEntryConf deviceEntryConf = new DeviceEntryConf();
        deviceEntryConf.setDeviceIpAddr("192.168.1.100");
        deviceEntryConf.setServicePort(1908);
        deviceEntryConf.setDeviceType("FCBR-100M");
        deviceEntryConf.setSn("200402003");
        deviceEntryConf.setTransferMethod(DeviceEntryDef.TRANSPORT_TCP);
        Fcbr100SocketClient fcbr100SocketClient = new Fcbr100SocketClient();
        fcbr100SocketClient.handleClientSocket(deviceEntryConf, System.currentTimeMillis());
    }

    public void initConn() {
        closeAll();

        // 获取提供Socket服务的设备列表。
        // 需要为每个提供Socket服务的设备，创建Socket客户端连接。
        Long currTimeInMillis = System.currentTimeMillis();
        commonSocketService.addTimeInMillis("handleClientSocket", currTimeInMillis);
        DeviceEntryConf deviceEntryConf = new DeviceEntryConf();
        deviceEntryConf.setDeviceType(SystemSettingStore.getDeviceType());
        deviceEntryConf.setSn(SystemSettingStore.getSn());
        deviceEntryConf.setDeviceIpAddr(SystemSettingStore.getIpAddr());
        deviceEntryConf.setServicePort(SystemSettingStore.getPort());
        handleClientSocket(deviceEntryConf, currTimeInMillis);
    }

    private void handleClientSocket(DeviceEntryConf deviceEntryConf, Long currTimeInMillis) {
        String deviceType = deviceEntryConf.getDeviceType();
        String sn = deviceEntryConf.getSn();
        String ipAddr = deviceEntryConf.getDeviceIpAddr();
        int port = deviceEntryConf.getServicePort();

        Thread newThread = new Thread(() -> {
            /**
             * <pre>
             *     外循环（进入重新创建Socket的流程）
             *     （1）为了确保前一个Socket出现异常时，能够重新进入创建Socket客户端连接的流程，增加了该While循环。
             *     （2）该While循环只是为了能够重新进入创建Socket客户端连接的流程，暂无其它作用。
             *     （3）增加程序的健壮性，在任何情况下能够保持有一个Socket连接可用。
             *     （4）如果设备一直无法连接，会导致隔30秒再次进到这里，并抛异常。
             * </pre>
             */
            while (true) {
                // 如果有别的Socket要打开，而该监听还在持续进行，则先关闭该Socket，防止一件事情由两个Socket处理。
                if (!commonSocketService.containsTimeInMillis("handleClientSocket", currTimeInMillis)) {
                    return;
                }

                Socket socket = null;
                InputStream inputStream = null;
                byte[] byteBuf = new byte[DeviceEntryDef.READ_BUFSIZE];

                /**
                 * <pre>
                 *     单个Socket创建的最外层
                 *     （1）正常情况一个设备只有一个Socket客户端连接。
                 *     （2）当前一个Socket客户端连接异常时，在此开始进入重新创建的逻辑。
                 * </pre>
                 */
                try {
                    // 如果有已经打开的Socket，先关闭。
                    commonSocketService.closeClientSocket(sn);

                    log.info("开始为设备{}，创建Socket连接{}:{}", sn, ipAddr, port);

                    socket = new Socket(ipAddr, port);

                    log.info("设备{}，创建Socket连接{}:{}完成", sn, ipAddr, port);
                    commonSocketService.addClientSocket(sn, socket);

                    // 有些设备如FCBR-100M，需要客户端连接之后发送心跳，才会持续输出数据。
                    byte[] crcAndEncodeBytes = ProtocolService.buildReq((byte)0x00, deviceType, sn, null);
                    if (crcAndEncodeBytes != null) {
                        commonSocketService.send(deviceType, sn, socket, crcAndEncodeBytes);
                        log.info("设备{}，创建时单次心跳发送完成", sn);
                    }

                    log.info("设备{}，开始获取输入流", sn);

                    inputStream = socket.getInputStream();

                    log.info("设备{}，输入流获取完成", sn);

                    /**
                     * <pre>
                     *     内循环（每个已创建的Socket的处理总循环）
                     *     （1）在有数据可读之前，防止结束Socket。
                     *     （2）持续等待直到有数据可读。
                     *     （3）正常情况，不需要该层While循环和if分支的判断。但考虑各种特殊情况，还是补充了该层While循环和if分支的判断逻辑。
                     *     （4）如果一直无数据可读，是否要有超出次数跳出该内循环，重新创建Socket。
                     * </pre>
                     */
                    int continousEmptyCycleCount = 0;
                    while (true) {
                        if (inputStream.available() > 0) {
                            // 持续空循环计数器清零，后续可通过此判断是否结束该Socket。
                            continousEmptyCycleCount = 0;

                            log.info("设备{}，有可读数据，开始阻塞持续读取", sn);

                            /**
                             * <pre>
                             *     数据读取循环（每个已创建的Socket的数据读取循环）
                             *     （1）一般情况，Socket建立后，只要有数据，会一直阻塞在这个While循环内，持续读取数据，永远不会退出。
                             *     （2）如果跳出此While循环，则Socket结束或存在问题，需要跳出内循环，进入外循环，重新创建Socket进行读取。
                             *     （3）Socket结束除非双方约定具体的内容，否则只有当Socket关闭或异常时结束。
                             * </pre>
                             */
                            int readCount = 0;
                            while ((readCount = inputStream.read(byteBuf)) != -1) {
                                // 如果有别的Socket要打开，而该监听还在持续进行，则先关闭该Socket，防止一件事情由两个Socket处理。
                                if (!commonSocketService.containsTimeInMillis("handleClientSocket", currTimeInMillis)) {
                                    commonSocketService.closeClientSocket(sn, socket);
                                    return;
                                }

                                byte[] destBuf = new byte[readCount];
                                System.arraycopy(byteBuf, 0, destBuf, 0, readCount);

                                if (log.isDebugEnabled()) {
                                    log.debug("设备{}，收到部分数据", sn);
                                }

                                // 目前只实现FCBR-100M，其它暂时没有需求，先不实现。
                                List<byte[]> dataFrames = ProtocolService.handleFramePart(deviceType, sn, destBuf);
                                if (dataFrames != null && deviceType.startsWith("FCBR-100")) {
                                    dataFrames.forEach(dataFrame -> {
                                        if (log.isDebugEnabled()) {
                                            log.debug("设备{}，收到完整一帧数据并提交entry308ServiceImpl处理", sn);
                                        }

                                        try {
                                            entryService.handleEntryData(dataFrame, ProductDef.PRODUCT_CODE_308,
                                                deviceType, sn, null);
                                        } catch (Exception e) {
                                            log.error("设备{}，完整帧提交entry308ServiceImpl处理时异常", sn, e);
                                        }
                                    });
                                }
                            }

                            // 如果到达此处，则Socket结束或存在问题，需要跳出内循环，进入外循环，重新创建Socket进行读取。
                            log.info("设备{}，数据读取结束，跳出内循环，进入外循环，重新创建Socket进行读取", sn);
                            break;
                        } else {
                            // 一般情况，不会进到这里。只有在开始很短一段时间，Socket输入流没有准备好，才会进入这里。
                            log.info("设备{}，持续计数{}次无可读数据，休眠2秒后重新进行读取", sn, continousEmptyCycleCount);

                            try {
                                Thread.sleep(DeviceEntryDef.READ_EMPTY_CYCLE_INTERVAL);
                            } catch (Exception e) {
                                log.error("设备{}，读取Socket休眠时异常", sn, e);
                            }

                            continousEmptyCycleCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("设备{}，Socket数据接收{}:{}异常。将释放资源，休眠30秒后，重新创建Socket进行读取", sn, ipAddr, port, e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            log.error("设备{}，Socket输入流关闭异常", sn, e);
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            log.error("设备{}，Socket关闭异常", sn, e);
                        }
                    }
                    try {
                        // 出现异常，休眠30秒，防止频繁创建连接。
                        Thread.sleep(DeviceEntryDef.RECREATE_INTERVAL);
                    } catch (Exception e) {
                        log.error("设备{}，读取Socket休眠30秒时异常", sn, e);
                    }
                }
            }
        });

        try {
            log.info("设备{}，Socket创建{}:{}和数据读取线程开始启动", sn, ipAddr, port);
            newThread.start();
            batchThreadHandler.add(newThread);
            log.info("设备{}，Socket创建{}:{}和数据读取线程启动完成", sn, ipAddr, port);
        } catch (Exception e) {
            log.error("设备{}，Socket创建{}:{}和数据读取线程启动异常", sn, ipAddr, port, e);
        }
    }

    public void closeAll() {
        batchThreadHandler.stopAll();
    }
}
