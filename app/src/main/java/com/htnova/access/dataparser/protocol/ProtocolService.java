package com.htnova.access.dataparser.protocol;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.dataparser.pojo.FrameState;
import com.htnova.access.dataparser.utils.ProtocolUtil;
import com.htnova.access.pojo.dto.DeviceConfig;
import com.htnova.access.sysconfig.constdef.MsgDef;
import com.htnova.access.sysconfig.constdef.ProductDef;

/**
 * 协议解析对外提供服务。
 */
public class ProtocolService {
    private static final Logger log = LoggerFactory.getLogger(ProtocolService.class);

    private static final Map<String, byte[]> lastFrameStates = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> lastInstructStates = new ConcurrentHashMap<>();

    private static final String noProtocolPattern = "==========设备类型：%s，设备号：%s无对应的解析协议==========";

    private static final AbstractDeviceProt protocol308 = new Device308Impl();
    private static final AbstractDeviceProt protocolFcbr100m = new DeviceFcbr100mImpl();

    // 不用作协议解析，协议解析直接用protocol308，包括protocol308v1和protocol308v2。
    private static final AbstractDeviceProt protocol308v2 = new Device308v2Impl();

    /**
     * <pre>
     *     设备协议解析抽象方法，由不同设备具体实现。一般分为如下几种情况：
     *     （1）支持klv的308v2协议，调用handleKlvData方法，实现handleKlvFrame，具体每一个模块的解析，调用模块的解析方法。
     *     （2）非308v2协议，直接调用相应模块的解析。
     * </pre>
     *
     * @param dataFrame
     *            协议数据帧。
     * @param productCode
     *            产品编码。
     * @param deviceType
     *            设备型号，由于向下兼容，旧的MQTT协议中没有体现deviceType，因此可能是笼统的值，如白盒子FCBR-100，拉曼类CR1600II。
     * @param sn
     *            设备号，由于向下兼容，旧的MQTT协议中没有体现sn，因此可能是null。
     * @return 包含了协议解析数据的设备数据实体。
     * @throws Exception
     */
    public static AbstractDevice getProtocolData(byte[] dataFrame, int productCode, String deviceType, String sn)
        throws Exception {
        // FCBR-100M协议结构一致，但协议单独编号，因此单独处理。
        if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {

            return protocolFcbr100m.analysisProtocol(dataFrame, productCode, deviceType, sn);

        }
        // 由于FCBR设备统一解析，此处以FCBR开头来判断。
        else if (deviceType.startsWith("FCBR")) {

            return protocol308.analysisProtocol(dataFrame, productCode, deviceType, sn);

        } else {
            log.error("设备类型：{}，设备号：{}，无对应的解析协议", deviceType, sn);

            throw new Exception(String.format(noProtocolPattern, deviceType, sn));
        }
    }

    /**
     * 构建向设备发送请求的数据结构。如果没有这种业务需求，不需要实现。
     *
     * @param msgId
     *            消息号。
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param params
     *            额外的参数信息。
     * @return msgId对应的请求数据结构。
     */
    public static byte[] buildReq(byte msgId, String deviceType, String sn, Map<String, Object> params) {
        // 只针对需要的设备型号，其余设备型号不处理。
        if (ProductDef.DEVICE_TYPE_FCBR100CP.equals(deviceType)) {
            return protocol308v2.buildReq(msgId, deviceType, sn, params);
        }

        if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            return protocolFcbr100m.buildReq(msgId, deviceType, sn, params);
        }

        return null;
    }

    /**
     * 一帧数据分多次发送，按deviceType、sn分类，以后可能解析逻辑会有变化。
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param framePart
     *            一帧完整或部分数据。
     * @return 是否有正常帧数据对应的字节数组。
     */
    public static List<byte[]> handleFramePart(String deviceType, String sn, byte[] framePart) {
        byte[] currFramePart = framePart;
        byte[] lastFramePart = lastFrameStates.get(sn);

        // 对当前拼接后的数据进行处理。
        FrameState frameState = ProtocolUtil.getFrameState308v2(currFramePart, lastFramePart);
        if (frameState != null) {
            // 还有剩余的非完整帧，需要记录。
            if (frameState.getRemainData() != null) {
                lastFrameStates.put(sn, frameState.getRemainData());
            } else {
                if (lastFrameStates.containsKey(sn)) {
                    lastFrameStates.remove(sn);
                }
            }

            return frameState.getCurrFrames();
        } else {
            if (lastFrameStates.containsKey(sn)) {
                lastFrameStates.remove(sn);
            }

            return null;
        }
    }

    /**
     * <pre>
     *     部分设备协议解析时，需要用到配置信息，该信息也从协议中获取，该信息相对稳定，需要缓存。
     *     该方法用以判断设备需要的配置信息是否存在。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @return 存在返回true，否则返回false。
     */
    public static boolean containsConfig(String deviceType, String sn) {
        // 只针对需要的设备型号，其余设备型号不处理。
        if (ProductDef.DEVICE_TYPE_FCBR100CP.equals(deviceType)) {
            return protocol308v2.containsConfig(deviceType, sn);
        }

        if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            return protocolFcbr100m.containsConfig(deviceType, sn);
        }

        return true;
    }

    /**
     * 获取设备配置信息。
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @return 设备配置信息实体。
     */
    public static DeviceConfig getConfig(String deviceType, String sn) {
        return HisDeviceConfig.getConfig(deviceType, sn);
    }

    /**
     * <pre>
     *     部分设备协议解析时，需要用到配置信息，该信息也从协议中获取，该信息相对稳定，需要缓存。
     *     该方法用以清除设备的配置信息。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     */
    public static void clearConfig(String deviceType, String sn) {
        // 只针对需要的设备型号，其余设备型号不处理。
        if (ProductDef.DEVICE_TYPE_FCBR100CP.equals(deviceType)) {
            protocol308v2.clearConfig(deviceType, sn);
        }

        if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            protocolFcbr100m.clearConfig(deviceType, sn);
        }
    }

    /**
     * 判断消息号是否时响应指令，对响应指令的处理会有所不同。
     *
     * @param msgId
     *            消息号。
     * @return 是响应指令返回true，否则返回false。
     */
    public static boolean isFcbrInstruct(Number msgId) {
        return NumberUtil.isValueIn(msgId, MsgDef.MID_308_INSTRUCTS);
    }

    public static void setLastInstructResult(String deviceType, String sn, byte msgId, boolean result) {
        lastInstructStates.put(sn + "_" + msgId, result);
    }

    public static boolean isLastInstructSuccess(String deviceType, String sn, byte msgId) {
        boolean success = false;
        if (lastInstructStates.containsKey(sn + "_" + msgId)) {
            success = lastInstructStates.get(sn + "_" + msgId);
            lastInstructStates.remove(sn + "_" + msgId);
        }
        return success;
    }

    public static void clearAll() {
        lastFrameStates.clear();
        lastInstructStates.clear();
        HisDeviceConfig.clearAllConfig();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // 一帧数据分多次发送，解析测试验证。
    ////////////////////////////////////////////////////////////////////////////////
    private static final byte[] oneFrameExactly =
        {85, 0, 21, 119, -96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 0, 1, -14, 35};
    private static final byte[] oneFrame2Part1Datas =
        {5, 0, 0, 0, 2, 0, 26, 35, 85, 0, 21, 119, -96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 1, 0, 55, 35};
    private static final byte[] oneFrame2Part2Datas =
        {85, 0, 21, 119, -96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 1, 0, 55, 35, 85, 0, 21, 119, -96, -118, -47};
    private static final byte[] oneFrame3PartDatas = {5, 0, 0, 0, 0, 1, -14, 35, 85, 0, 21, 119, -96, -118, -47, 7, 65,
        0, 8, 34, 0, 5, 0, 0, 0, 1, 0, 55, 35, 85, 0, 21, 119, -96, -118, -47};
    private static final byte[] twoFrameExactly = {85, 0, 21, 119, -96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 1, 0,
        55, 35, 85, 0, 21, 119, -96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 2, 0, 26, 35};

    private static final byte[] framePart1 = {5, 0, 0, 0, 2, 0, 26, 35, 85, 0, 21, 119, -96, -118, -47, 7};
    private static final byte[] framePart2 = {65, 0, 8, 34, 0, 5, 0, 0, 0, 3, 0};
    private static final byte[] framePart3 = {-18, 35, 85, 0, 21, 119};
    private static final byte[] framePart4 =
        {-96, -118, -47, 7, 65, 0, 8, 34, 0, 5, 0, 0, 0, 4, 0, 64, 35, 85, 0, 21, 119, -96, -118};

    private static final String sn = "60200001";
    private static final String deviceType = "GD606";

    public static void main(String[] args) {
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.oneFrameExactly);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.oneFrame2Part1Datas);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.oneFrame2Part2Datas);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.oneFrame3PartDatas);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.twoFrameExactly);
        //
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.framePart1);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.framePart2);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.framePart3);
        // ProtocolService.handleFramePart(deviceType, sn, ProtocolService.framePart4);

        // 八音盒测试。
        byte[] data = {85, 1, 56, -1, -1, -1, -1, -1, -1, -1, -1, 5, 65, 1, 43, 0, 38, 7, 0, 0, 3, 0, 84, 0, 0, 1, 38,
            7, 0, 1, 3, 0, 0, 0, 0, 1, 38, 7, 0, 2, 3, 0, 0, 0, 0, 0, 38, 7, 0, 3, 3, 0, 0, 0, 0, 1, 38, 7, 0, 4, 3, 0,
            64, 2, 0, 0, 38, 7, 0, 5, 3, 0, 114, 8, 0, 2, 38, 7, 0, 6, 3, 0, 0, 0, 0, 1, 38, 7, 0, 7, 3, 0, 0, 0, 0, 1,
            38, 7, 0, 8, 3, 0, 0, 0, 0, 0, 38, 7, 0, 11, 3, 0, 0, 0, 0, 1, 38, 7, 0, 12, 3, 0, 0, 0, 0, 1, 38, 7, 0, 13,
            3, 0, 0, 0, 0, 0, 38, 7, 0, 14, 3, 0, 0, 0, 0, 2, 38, 7, 0, 15, 3, 0, 0, 0, 0, 3, 38, 7, 0, 16, 3, 0, 0, 0,
            0, 1, 38, 7, 0, 17, 3, 0, 0, 0, 0, 1, -111, 7, 0, 0, 3, 0, 0, 0, 0, 0, -111, 7, 0, 1, 3, 0, 0, 0, 0, 0,
            -111, 7, 0, 2, 3, 0, 0, 0, 0, 0, -111, 7, 0, 3, 3, 0, 0, 0, 0, 0, -111, 7, 0, 4, 3, 0, 0, 0, 0, 0, -111, 7,
            0, 5, 3, 0, 0, 0, 0, 0, -111, 7, 0, 6, 3, 0, 0, 0, 0, 0, 101, 2, 0, 0, 21, 102, 2, 0, 0, 19, 101, 2, 0, 1,
            25, 102, 2, 0, 1, 15, 101, 2, 0, 2, 25, 102, 2, 0, 2, 20, 100, 2, 0, 0, 86, 49, 2, 0, 90, 0, 50, 1, 0, 0,
            51, 1, 0, 0, 52, 1, 0, 0, 53, 1, 0, 0, 54, 1, 0, 0, 103, 5, 0, 3, -120, 2, -118, 2, -17, 35};
        try {
            ProtocolService.getProtocolData(data, 0x05, "FCBR-100M", "2004020003");
        } catch (Exception e) {
            log.error("解析协议数据异常", e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////
}
