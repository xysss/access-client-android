package com.htnova.access.dataparser.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.dataparser.pojo.FrameState;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 协议工具类，为协议解析提供基本的支持。
 *     协议结构总体说明：
 *     （一）协议分为：帧头、包头、数据、帧尾四部分
 *     （1）帧头包含3个字节
 *         FS – 帧起始字节，1个字节，统一固定为0x55（二进制01010101，不容易被干扰）
 *         FL –帧长度，2个字节，大端字节序（高字节在前），数据长度包含帧头和帧尾
 *     （2）包头包含8个字节
 *         DSN – Device SN，设备序列号，4个字节，大端字节序。比如2008040032
 *         PID – Product ID，产品ID，1个字节，华泰统一分配，FCBR-100控制主板的ID为0x05，VOC模块的ID为0x06
 *         MID – Message ID，消息ID，1个字节，定义消息，详情见消息列表
 *         ML – 消息数据长度，2个字节，大端字节序（高字节在前），数据长度不包含包头、帧头和帧尾
 *     （3）数据Data的长度为ML个字节，具体数据格式依消息而定
 *     （4）帧尾包含2个字节
 *         CRC – CRC8校验值，1个字节，校验针对帧头和包数据，不包含CRC和FE
 *         FE – 帧结束字节，1个字节，统一固定为0x23
 *     注意：
 *     帧的接收方需要对帧的有效性进行检查，包括FS、FL、CRC和FE，此时不应对包的有效性进行检查。禁止采用根据长度或者帧尾简单判断帧结束的方式。
 *     帧的接收方需要有容错机制，一旦帧的有效性检查失败，需要及时丢弃该帧，并开始检测下一个帧头。
 *     （二）数据转码：因为帧同步的需求，除了帧起始字节（FS）以外，帧内的数据不允许出现与FS一致的数值，即0x55。因此需要对帧的数据做转码操作，规则如下：
 *     （1）发送方：如遇0x55，转码为0xFF 0x00；如遇0xFF，转码为0xFF 0xFF
 *     （2）接收方：如遇0xFF 0xFF，转码为0xFF；如遇0xFF 0x00，转码为0x55
 *     这从理论上确保了帧头的唯一性。
 *     注意，转码后的字节不能重新参与转码。帧长度是转码前的长度，它也需要参与转码。
 *     转码的操作是发送的最后一步，接收的第一步，帧长度、CRC校验等效性验证在转码后进行，对应用层透明。
 * </pre>
 */
@Slf4j
public class ProtocolUtil {
    // 几个需要转码的字节或字节数组的定义。
    private static final byte[] ff00For55 = new byte[] {(byte)0xFF, 0x00};
    private static final byte[] ffffForFF = new byte[] {(byte)0xFF, (byte)0xFF};
    private static final byte frameStart308v2 = 0x55;
    private static final byte frameStart308v1 = 0x40;
    private static final byte frameEnd308v2 = 0x23;
    private static final byte frameByteFF = (byte)0xFF;
    private static final byte frameByte00 = (byte)0x00;
    private static final int auxDataLength = 13;
    private static final int minFrameLength = 9; // GD606为9，FCBR-100系列为13。

    // crc表格，用来在crc校验时进行查表获取校验后的字节。
    private static final int[] crcTable = {0x00, 0x31, 0x62, 0x53, 0xc4, 0xf5, 0xa6, 0x97, 0xb9, 0x88, 0xdb, 0xea, 0x7d,
        0x4c, 0x1f, 0x2e, 0x43, 0x72, 0x21, 0x10, 0x87, 0xb6, 0xe5, 0xd4, 0xfa, 0xcb, 0x98, 0xa9, 0x3e, 0x0f, 0x5c,
        0x6d, 0x86, 0xb7, 0xe4, 0xd5, 0x42, 0x73, 0x20, 0x11, 0x3f, 0x0e, 0x5d, 0x6c, 0xfb, 0xca, 0x99, 0xa8, 0xc5,
        0xf4, 0xa7, 0x96, 0x01, 0x30, 0x63, 0x52, 0x7c, 0x4d, 0x1e, 0x2f, 0xb8, 0x89, 0xda, 0xeb, 0x3d, 0x0c, 0x5f,
        0x6e, 0xf9, 0xc8, 0x9b, 0xaa, 0x84, 0xb5, 0xe6, 0xd7, 0x40, 0x71, 0x22, 0x13, 0x7e, 0x4f, 0x1c, 0x2d, 0xba,
        0x8b, 0xd8, 0xe9, 0xc7, 0xf6, 0xa5, 0x94, 0x03, 0x32, 0x61, 0x50, 0xbb, 0x8a, 0xd9, 0xe8, 0x7f, 0x4e, 0x1d,
        0x2c, 0x02, 0x33, 0x60, 0x51, 0xc6, 0xf7, 0xa4, 0x95, 0xf8, 0xc9, 0x9a, 0xab, 0x3c, 0x0d, 0x5e, 0x6f, 0x41,
        0x70, 0x23, 0x12, 0x85, 0xb4, 0xe7, 0xd6, 0x7a, 0x4b, 0x18, 0x29, 0xbe, 0x8f, 0xdc, 0xed, 0xc3, 0xf2, 0xa1,
        0x90, 0x07, 0x36, 0x65, 0x54, 0x39, 0x08, 0x5b, 0x6a, 0xfd, 0xcc, 0x9f, 0xae, 0x80, 0xb1, 0xe2, 0xd3, 0x44,
        0x75, 0x26, 0x17, 0xfc, 0xcd, 0x9e, 0xaf, 0x38, 0x09, 0x5a, 0x6b, 0x45, 0x74, 0x27, 0x16, 0x81, 0xb0, 0xe3,
        0xd2, 0xbf, 0x8e, 0xdd, 0xec, 0x7b, 0x4a, 0x19, 0x28, 0x06, 0x37, 0x64, 0x55, 0xc2, 0xf3, 0xa0, 0x91, 0x47,
        0x76, 0x25, 0x14, 0x83, 0xb2, 0xe1, 0xd0, 0xfe, 0xcf, 0x9c, 0xad, 0x3a, 0x0b, 0x58, 0x69, 0x04, 0x35, 0x66,
        0x57, 0xc0, 0xf1, 0xa2, 0x93, 0xbd, 0x8c, 0xdf, 0xee, 0x79, 0x48, 0x1b, 0x2a, 0xc1, 0xf0, 0xa3, 0x92, 0x05,
        0x34, 0x67, 0x56, 0x78, 0x49, 0x1a, 0x2b, 0xbc, 0x8d, 0xde, 0xef, 0x82, 0xb3, 0xe0, 0xd1, 0x46, 0x77, 0x24,
        0x15, 0x3b, 0x0a, 0x59, 0x68, 0xff, 0xce, 0x9d, 0xac,};

    private static byte[] head308v2 = new byte[] {0x55};
    private static byte[] end308v2 = new byte[] {0x23};

    // 工具类不需要外部构造。
    private ProtocolUtil() {}

    /**
     * FCBR内部用的8位CRC校验码。
     *
     * @param rawBytes
     *            原始数据。
     * @return 校验码。
     */
    public static byte calcCrc8(byte[] rawBytes) {
        byte crcByte = 0x00;

        for (byte abyte : rawBytes) {
            crcByte = (byte)crcTable[(crcByte & 0xFF) ^ (abyte & 0xFF)];
        }

        return crcByte;
    }

    /**
     * 遵循Modbus协议的CRC16校验码。
     *
     * @param rawBytes
     *            原始数据。
     * @return 校验码。
     */
    public static int calcCrc16(byte[] rawBytes) {
        int crcValue = 0x0000ffff;
        int polynomial = 0x0000a001;

        int i, j;
        for (i = 0; i < rawBytes.length; i++) {
            crcValue ^= ((int)rawBytes[i] & 0x000000ff);

            for (j = 0; j < 8; j++) {
                if ((crcValue & 0x00000001) != 0) {
                    crcValue >>= 1;
                    crcValue ^= polynomial;
                } else {
                    crcValue >>= 1;
                }
            }
        }

        return crcValue;
    }

    /**
     * 遵循Modbus协议的CRC16校验码，高字节在前，低字节在后。
     *
     * @param rawBytes
     *            原始数据。
     * @return 校验码。
     */
    public static int calcCrc16BigEndian(byte[] rawBytes) {
        int crcValue = 0xffff;
        int polynomial = 0xa001;
        int low, high, crc_flag;;

        int i, j;
        for (i = 0; i < rawBytes.length; i++) {
            low = crcValue & 0x00ff;
            high = crcValue & 0xff00;
            crcValue = low ^ ((int)rawBytes[i] & 0xff);
            crcValue = crcValue + high;
            for (j = 0; j < 8; j++) {
                crc_flag = crcValue & 0x0001;
                crcValue = crcValue >> 1;
                if (crc_flag == 1) {
                    crcValue = crcValue ^ polynomial;
                }
            }
        }

        return crcValue;
    }

    /**
     * 遵循Modbus协议的CRC16校验码。
     *
     * @param rawBytes
     *            原始数据。
     * @param start
     *            起始位置。
     * @param end
     *            结束位置。
     * @return 校验码。
     */
    public static byte[] calcGemininCrc16(byte[] rawBytes, int start, int end) {
        short wcrc = (short)0XFFFF;

        byte temp;
        for (int i = start; i < end; i++) {
            temp = (byte)(rawBytes[i] & 0X00FF);
            wcrc ^= temp;

            for (int j = 0; j < 8; j++) {
                if ((wcrc & 0X0001) == 1) {
                    wcrc >>= 1;
                    wcrc ^= 0XA001;
                } else {
                    wcrc >>= 1;
                }
            }
        }

        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte)(wcrc & 0xFF);
        crcBytes[1] = (byte)((wcrc >> 8) & 0xFF);

        return crcBytes;
    }

    /**
     * 对数据完成校验工作，头、尾、crc内容、总长度验证，以判断是否为有效帧。
     *
     * @param decodeFrameBytes
     *            解码后的协议帧数据。
     * @return 有效返回true，否则返回false。
     */
    public static boolean isValid308v2Frame(byte[] decodeFrameBytes) {
        // 验证头部
        for (int i = 0; i < head308v2.length; i++) {
            if (head308v2[i] != decodeFrameBytes[i]) {
                log.error("isValid308v2Frame 验证头部不合法，返回false");
                return false;
            }
        }

        // 验证尾部
        for (int i = 0; i < end308v2.length; i++) {
            if (end308v2[i] != decodeFrameBytes[decodeFrameBytes.length - end308v2.length + i]) {
                log.error("isValid308v2Frame 验证尾部不合法，返回false");
                return false;
            }
        }

        // crc校验
        byte[] crcDataBytes = new byte[decodeFrameBytes.length - 2];
        System.arraycopy(decodeFrameBytes, 0, crcDataBytes, 0, decodeFrameBytes.length - 2);
        byte crcByte = ProtocolUtil.calcCrc8(crcDataBytes);
        if (crcByte != decodeFrameBytes[decodeFrameBytes.length - 2]) {
            log.error("isValid308v2Frame crc校验不合法，返回false");
            return false;
        }

        // 长度校验
        byte[] frameLengthBytes = new byte[] {decodeFrameBytes[1], decodeFrameBytes[2]};
        int frameLength = NumberUtil.bytes2Int(frameLengthBytes, true);
        return decodeFrameBytes.length >= frameLength;
    }

    private static boolean isValid308v2FrameNoLog(byte[] decodeFrameBytes) {
        // 验证头部
        for (int i = 0; i < head308v2.length; i++) {
            if (head308v2[i] != decodeFrameBytes[i]) {
                return false;
            }
        }

        // 验证尾部
        for (int i = 0; i < end308v2.length; i++) {
            if (end308v2[i] != decodeFrameBytes[decodeFrameBytes.length - end308v2.length + i]) {
                return false;
            }
        }

        // crc校验
        byte[] crcDataBytes = new byte[decodeFrameBytes.length - 2];
        System.arraycopy(decodeFrameBytes, 0, crcDataBytes, 0, decodeFrameBytes.length - 2);
        byte crcByte = ProtocolUtil.calcCrc8(crcDataBytes);
        if (crcByte != decodeFrameBytes[decodeFrameBytes.length - 2]) {
            return false;
        }

        // 长度校验
        byte[] frameLengthBytes = new byte[] {decodeFrameBytes[1], decodeFrameBytes[2]};
        int frameLength = NumberUtil.bytes2Int(frameLengthBytes, true);
        return decodeFrameBytes.length >= frameLength;
    }

    /**
     * <pre>
     *     支持多帧一起，但不支持连续组帧。
     *     由于没有实现连续组帧的功能，当前完全依赖MQTT模块与发送程序配合，确保至少有一个完整帧的数据占绝大多数。
     *     将包中的数据分析后，分帧进行存储，v1和v2协议都适用。
     *     
     *     实现思路：先探测整个数据包中0x55、0x23,0x55、0x23的情况。然后根据探测结果分析。
     *     （1）没有0x55开头和0x23结尾的字节，本次数据为无效数据。
     *     （2）是否有符合0x55...0x23,0x55...0x23,0x55...的数据。
     *     （3）是否最后一个字节是0x23，整个数据包有一个完整帧。
     *     （4）则以相同范围的0x55和0x23组一个完整帧。
     *     
     *     todo by junzai：类似如下代码无法解析成功，需要进一步改进算法。
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 
     *     0x55, 0x00, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x05,0x00, 0x00, 0x00, 0x54, 0x23, 0x26, 0x21, 0x56, 0x67, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56,0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     *     
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x67, 0x23, 0x01,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     *     
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x67, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     * </pre>
     * 
     * @param rawDataPackage
     *            设备原始数据包。
     * @return 帧数据列表。
     */
    public static List<byte[]> getFrames308v2(byte[] rawDataPackage) {
        List<byte[]> rawDataFrames = new ArrayList<>();
        if (rawDataPackage == null || rawDataPackage.length == 0) {
            return rawDataFrames;
        }

        // 308v1协议，每次只收一帧数据，直接返回。
        if (rawDataPackage[0] == frameStart308v1) {
            rawDataFrames.add(rawDataPackage);
            return rawDataFrames;
        }

        // 先总体探测当前数据的情况，开头和结尾的情况。
        List<Integer> startPosFlags = new ArrayList<>();
        List<Integer> startEndPosFlags = new ArrayList<>();
        List<Integer> endPosFlags = new ArrayList<>();
        findStartEndPosFlags(rawDataPackage, startPosFlags, startEndPosFlags, endPosFlags);

        // 没有0x55开头和0x23结尾的字节，本次数据为无效数据。
        if (startPosFlags == null || startPosFlags.size() == 0 || endPosFlags == null || endPosFlags.size() == 0) {
            return rawDataFrames;
        }

        // 用以保存处理过的起始位置，以判断是否重复处理。
        Map<String, Boolean> handledPosMap = new HashMap<>();

        // 此处与组帧不同，组帧优先考虑是否整个数据有一个完整帧，即是否整体数据包以0x23结尾。
        // 1.优先级最高：从0x55开头往后看，是否有符合0x55...0x23,0x55...0x23,0x55...的数据。
        handleFrames2355Multi(rawDataFrames, rawDataPackage, handledPosMap, startPosFlags, startEndPosFlags);

        // 2.优先级其次：从0x55开头往后看，是否最后一个字节是0x23，整个数据包有一个完整帧。
        // 此处如果数据已经到最后了，则直接返回。
        // todo by junzai：由于只是判断最后字节是否为0x23，可能会一个帧中含有多帧数据，除非做进一步处理。
        if (handleFrames23End(rawDataFrames, rawDataPackage, handledPosMap, startPosFlags)) {
            return rawDataFrames;
        }

        // 3.优先级最低：如果以上都没有找到可能的完整帧，则以相同范围的0x55和0x23组一个完整帧。
        // todo by junzai：因为只是以0x55和0x23之间的数据作为一帧，没有做解码和校验，可能返回的不是一个正常帧。
        handleFrames5523Couple(rawDataFrames, rawDataPackage, handledPosMap, startPosFlags, endPosFlags);

        return rawDataFrames;
    }

    /**
     * 从0x55开头往后看，是否有符合0x55...0x23,0x55...0x23,0x55...的数据。
     * 
     * @param rawDataFrames
     *            保存返回的帧列表。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @param startEndPosFlags
     *            起始结束位置组合列表（0x23,0x55中0x55的位置）。
     */
    private static void handleFrames2355Multi(List<byte[]> rawDataFrames, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags, List<Integer> startEndPosFlags) {
        int startPosLength = startPosFlags.size();
        int startEndPosLength = startEndPosFlags.size();
        for (int i = 0; i < startPosLength; i++) {
            // 取出0x55的位置。
            int startPos = startPosFlags.get(i);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                break;
            }

            // 寻找符合0x55...0x23,0x55...0x23,0x55...的数据。
            for (int j = 0; j < startEndPosLength; j++) {
                // 取出startEndPos位置，为0x23,0x55中的0x55的位置。
                int startEndPos = startEndPosFlags.get(j);

                // startEndPos的0x55位置在startPos的0x55的位置后面，则可能存在有效帧。
                if ((startEndPos - startPos) >= minFrameLength) {
                    // 将0x55和0x23,0x55之间的数据放到返回列表中。
                    byte[] tempFrame = new byte[startEndPos - startPos];
                    System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                    rawDataFrames.add(tempFrame);
                    handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                    // 一个起始位置只能处理一次。
                    break;
                }
            }
        }
    }

    /**
     * 从0x55开头往后看，是否最后一个字节是0x23，整个数据包有一个完整帧。
     *
     * @param rawDataFrames
     *            保存返回的帧列表。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @return 是否有符合条件的数据，有true，无false。
     */
    private static boolean handleFrames23End(List<byte[]> rawDataFrames, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags) {
        int startPosLength = startPosFlags.size();
        int length = rawDataPackage.length;
        for (int i = 0; i < startPosLength; i++) {
            int startPos = startPosFlags.get(i);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            // 从当前0x55起始位置，到数据包最后一个字节，作为一帧数据。
            if (startPos < (length - 1) && rawDataPackage[length - 1] == frameEnd308v2
                && (length - startPos) >= minFrameLength) {
                byte[] tempFrame = new byte[length - startPos];
                System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                rawDataFrames.add(tempFrame);
                handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                // 已经到最后，直接返回。
                return true;
            }
        }

        return false;
    }

    /**
     * 如果以上都没有找到可能的完整帧，则以相同范围的0x55和0x23组一个完整帧。
     * 
     * @param rawDataFrames
     *            保存返回的帧列表。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @param endPosFlags
     *            结束位置列表（0x23的位置）。
     */
    private static void handleFrames5523Couple(List<byte[]> rawDataFrames, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags, List<Integer> endPosFlags) {
        // 解决如下情况：
        // STA（开始）:1----------50----------60
        // END（结束）:----7-8--------55-56----------64-65
        // 示例数据：0x01, 0x55, 0x10, 0x23, 0x24, 0x23, 0x30, 0x55, 0x30, 0x25, 0x23, 0x23, 0x20
        // 从最后一个0x55和0x23倒序搜索。
        int startPosLength = startPosFlags.size();
        int endPosLength = endPosFlags.size();
        int lastStartPos = 0;
        for (int i = startPosLength; i > 0; i--) {
            int startPos = startPosFlags.get(i - 1);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            for (int j = endPosLength; j > 0; j--) {
                int endPos = endPosFlags.get(j - 1);

                // 结束位置一定要在开始位置之后。
                if ((endPos - startPos) < (minFrameLength - 1)) {
                    break;
                }

                // 除了最后一个之外，结束位置不能在上次的开始位置之后。
                if (i != startPosLength && endPos >= lastStartPos) {
                    continue;
                }

                byte[] tempFrame = new byte[endPos - startPos + 1];
                System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                rawDataFrames.add(tempFrame);
                handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                // 一个起始位置只能处理一次。
                break;
            }

            // 重置上次开始位置。
            lastStartPos = startPos;
        }
    }

    /**
     * <pre>
     *     实现了连续组帧的功能，需要使用方配合，记录上次数据包的组帧状态。
     *     任意一次解析到的数据，都会返回一个本次解析的帧状态。有些时候，需要有多次数据拼接，才能获得有效帧。
     *     
     *     todo by junzai：类似如下代码无法解析成功，需要进一步改进算法。
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 
     *     0x55, 0x00, 0x0D, 0x00, 0x00, 0x00, 0x00, 0x05,0x00, 0x00, 0x00, 0x54, 0x23, 0x26, 0x21, 0x56, 0x67, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56,0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     *
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x67, 0x23, 0x01,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     *
     *     {0x01, 0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x67, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x56, 0x23,
     *     0x55, 0x03, 0x07, 0x11, 0x47, 0x26, 0x21, 0x23, 0x01}
     * </pre>
     * 
     * @param currDataPackage
     *            当前原始数据包，可能包含完整帧，也可能包含一部分帧，也可能包含多帧。
     * @param lastDataPackage
     *            上次处理后剩余到本次的数据包。
     * @return 根据当前原始数据包和上次剩余的数据包，分析后的帧状态。
     */
    public static FrameState getFrameState308v2(byte[] currDataPackage, byte[] lastDataPackage) {
        if (currDataPackage == null || currDataPackage.length == 0) {
            return null;
        }

        // 用以保存处理过的起始位置，以判断是否重复处理。
        Map<String, Boolean> handledPosMap = new HashMap<>();
        FrameState frameState = new FrameState();

        // 有完整帧，直接返回。
        if (handleFrameState23End(frameState, currDataPackage, handledPosMap)) {
            return frameState;
        }

        byte[] rawDataPackage = currDataPackage;
        if (lastDataPackage != null) {
            rawDataPackage = new byte[currDataPackage.length + lastDataPackage.length];

            System.arraycopy(lastDataPackage, 0, rawDataPackage, 0, lastDataPackage.length);
            System.arraycopy(currDataPackage, 0, rawDataPackage, lastDataPackage.length, currDataPackage.length);
        }

        // 先总体探测当前数据的情况，开头和结尾的情况。
        List<Integer> startPosFlags = new ArrayList<>();
        List<Integer> startEndPosFlags = new ArrayList<>();
        List<Integer> endPosFlags = new ArrayList<>();
        findStartEndPosFlags(rawDataPackage, startPosFlags, startEndPosFlags, endPosFlags);

        // 没有0x55开头的字节，本次数据为无效数据。由于是组帧，可以没有0x23结尾。
        if (startPosFlags == null || startPosFlags.size() == 0) {
            return null;
        }

        // 1.优先级最高：从0x55开头往后看，是否最后一个字节是0x23，整个数据包有一个完整帧。
        if (handleFrameState23End2(frameState, rawDataPackage, handledPosMap, startPosFlags)) {
            return frameState;
        }

        // 2.优先级其次：从0x55开头往后看，是否有符合0x55...0x23,0x55...0x23,0x55...的数据。
        handleFrameState2355Multi(frameState, rawDataPackage, handledPosMap, startPosFlags, startEndPosFlags);

        // 3.优先级最低：从0x55开头往后看，中间是否有插入的完整帧，以0x55开头，以0x23结尾。
        handleFrameState5523Insert(frameState, rawDataPackage, handledPosMap, startPosFlags, endPosFlags);

        // 4.以上三种情况都没有出现，找到最后一个0x55，之后的都作为剩余数据。
        if (frameState.isEmpty()) {
            handleFrameStateRemain(frameState, rawDataPackage, handledPosMap, startPosFlags);
        }

        // 重新整理remainData。
        byte[] remainData = frameState.getRemainData();
        if (remainData != null) {
            if (remainData.length == 0) {
                frameState.setRemainData(null);
            } else {
                if (remainData[remainData.length - 1] == frameEnd308v2) {
                    frameState.setRemainData(null);
                }
            }
        }

        return frameState;
    }

    /**
     * 有完整帧，直接返回。
     * 
     * @param frameState
     *            拼帧返回的实体。
     * @param currDataPackage
     *            当前原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @return 最后一个字节为0x23，返回true，否则false。
     */
    private static boolean handleFrameState23End(FrameState frameState, byte[] currDataPackage,
        Map<String, Boolean> handledPosMap) {
        int currLength = currDataPackage.length;
        for (int i = 0; i < currLength; i++) {
            if (currDataPackage[i] == frameStart308v2) {
                // 防止重复处理。
                if (handledPosMap.containsKey(Integer.toString(i))) {
                    continue;
                }

                // 从当前0x55起始位置，到数据包最后一个字节，作为一帧数据。
                if (i < (currLength - 1) && currDataPackage[currLength - 1] == frameEnd308v2
                    && (currLength - i) >= minFrameLength) {
                    byte[] tempFrame = new byte[currLength - i];
                    System.arraycopy(currDataPackage, i, tempFrame, 0, tempFrame.length);
                    frameState.addCurrFrame(tempFrame);
                    frameState.setRemainData(null);
                    handledPosMap.put(Integer.toString(i), Boolean.TRUE);

                    // 已经到最后，直接返回。
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 从0x55开头往后看，是否最后一个字节是0x23，整个数据包有一个完整帧。
     * 
     * @param frameState
     *            拼帧返回的实体。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @return 最后一个字节为0x23，返回true，否则false。
     */
    private static boolean handleFrameState23End2(FrameState frameState, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags) {
        int length = rawDataPackage.length;
        int startPosLength = startPosFlags.size();
        for (int i = 0; i < startPosLength; i++) {
            int startPos = startPosFlags.get(i);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            // 从当前0x55起始位置，到数据包最后一个字节，作为一帧数据。
            if (startPos < (length - 1) && rawDataPackage[length - 1] == frameEnd308v2
                && (length - startPos) >= minFrameLength) {
                byte[] tempFrame = new byte[length - startPos];
                System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                frameState.addCurrFrame(tempFrame);
                frameState.setRemainData(null);
                handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                // 已经到最后，直接返回。
                return true;
            }
        }
        return false;
    }

    /**
     * 从0x55开头往后看，是否有符合0x55...0x23,0x55...0x23,0x55...的数据。
     *
     * @param frameState
     *            拼帧返回的实体。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @param startEndPosFlags
     *            起始结束位置组合列表（0x23,0x55中0x55的位置）。
     */
    private static void handleFrameState2355Multi(FrameState frameState, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags, List<Integer> startEndPosFlags) {
        int length = rawDataPackage.length;
        int startPosLength = startPosFlags.size();
        int startEndPosLength = startEndPosFlags.size();
        for (int i = 0; i < startPosLength; i++) {
            // 取出0x55的位置。
            int startPos = startPosFlags.get(i);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            // 寻找符合0x55...0x23,0x55...0x23,0x55...的数据。
            for (int j = 0; j < startEndPosLength; j++) {
                // 取出startEndPos位置，为0x23,0x55中的0x55的位置。
                int startEndPos = startEndPosFlags.get(j);

                // startEndPos的0x55位置在startPos的0x55的位置后面，则可能存在有效帧。
                if ((startEndPos - startPos) >= minFrameLength) {
                    // 将0x55和0x23,0x55之间的数据放到返回列表中。
                    byte[] tempFrame = new byte[startEndPos - startPos];
                    System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                    frameState.addCurrFrame(tempFrame);
                    byte[] remainData = new byte[length - startEndPos];
                    System.arraycopy(rawDataPackage, startEndPos, remainData, 0, remainData.length);
                    frameState.setRemainData(remainData);
                    handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                    // 一个起始位置只能处理一次。
                    break;
                }
            }
        }
    }

    /**
     * 从0x55开头往后看，中间是否有插入的完整帧，以0x55开头，以0x23结尾。
     *
     * @param frameState
     *            拼帧返回的实体。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @param endPosFlags
     *            结束位置列表（0x23的位置）。
     */
    private static void handleFrameState5523Insert(FrameState frameState, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags, List<Integer> endPosFlags) {
        int length = rawDataPackage.length;
        int startPosLength = startPosFlags.size();
        int endPosLength = endPosFlags.size();
        for (int i = 0; i < startPosLength; i++) {
            int startPos = startPosFlags.get(i);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            for (int j = 0; j < endPosLength; j++) {
                int endPos = endPosFlags.get(j);

                // 结束位置一定要在开始位置之后。
                if ((endPos - startPos) < (minFrameLength - 1)) {
                    continue;
                }

                byte[] tempFrame = new byte[endPos - startPos + 1];
                System.arraycopy(rawDataPackage, startPos, tempFrame, 0, tempFrame.length);
                byte[] decodeTempFrame = decode308v2(tempFrame);

                // 中间存在有效的完整帧，由于0x23不唯一，无法确定哪个用于区分一个完整帧，所以通过校验来识别。
                if (isValid308v2FrameNoLog(decodeTempFrame)) {
                    frameState.addCurrFrame(tempFrame);
                    handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

                    // 除去中间完整帧后的数据重新拼接，作为剩余数据。
                    if (endPos < (length - 1)) {
                        if (i > 0) {
                            // 开始位置在中间，需要将上一个起始位置到当前起始位置之间的数据，与结束位置之后的数据拼接为剩余数据。
                            int lastStartPos = startPosFlags.get(i - 1);
                            byte[] remainData = new byte[(startPos - lastStartPos) + (length - endPos - 1)];
                            System.arraycopy(rawDataPackage, lastStartPos, remainData, 0, (startPos - lastStartPos));
                            System.arraycopy(rawDataPackage, (endPos + 1), remainData, (startPos - lastStartPos),
                                (length - endPos - 1));
                            frameState.setRemainData(remainData);
                        } else {
                            // 开始位置为第一个，则只要将结束位置之后的数据作为剩余数据即可。
                            byte[] remainData = new byte[length - endPos];
                            System.arraycopy(rawDataPackage, endPos + 1, remainData, 0, remainData.length);
                            frameState.setRemainData(remainData);
                        }
                    } else {
                        frameState.setRemainData(null);
                    }

                    // 一个起始位置只能处理一次。
                    break;
                }
            }
        }
    }

    /**
     * 前面情况都没有出现，找到最后一个0x55，之后的都作为剩余数据。
     *
     * @param frameState
     *            拼帧返回的实体。
     * @param rawDataPackage
     *            原始数据包。
     * @param handledPosMap
     *            保存处理过的起始位置的映射（处理过的0x55的位置）。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     */
    private static void handleFrameStateRemain(FrameState frameState, byte[] rawDataPackage,
        Map<String, Boolean> handledPosMap, List<Integer> startPosFlags) {
        int length = rawDataPackage.length;
        int startPosLength = startPosFlags.size();
        for (int i = startPosLength; i > 0; i--) {
            int startPos = startPosFlags.get(i - 1);

            // 防止重复处理。
            if (handledPosMap.containsKey(Integer.toString(startPos))) {
                continue;
            }

            byte[] remainData = new byte[length - startPos];
            System.arraycopy(rawDataPackage, startPos, remainData, 0, remainData.length);
            frameState.setRemainData(remainData);
            handledPosMap.put(Integer.toString(startPos), Boolean.TRUE);

            // 只处理最后一个0x55及之后的数据作为剩余数据，直接返回。
            return;
        }
    }

    /**
     * 查找startPosFlags、startEndPosFlags、endPosFlags列表。
     * 
     * @param rawDataPackage
     *            原始数据包。
     * @param startPosFlags
     *            起始位置列表（0x55的位置）。
     * @param startEndPosFlags
     *            起始结束位置组合列表（0x23,0x55中0x55的位置）。
     * @param endPosFlags
     *            结束位置列表（0x23的位置）。
     */
    private static void findStartEndPosFlags(byte[] rawDataPackage, List<Integer> startPosFlags,
        List<Integer> startEndPosFlags, List<Integer> endPosFlags) {
        int length = rawDataPackage.length;

        // 先总体探测当前数据的情况，开头和结尾的情况。
        for (int i = 0; i < length; i++) {
            if (rawDataPackage[i] == frameStart308v2) {
                startPosFlags.add(i);
            }

            if (rawDataPackage[i] == frameEnd308v2) {
                endPosFlags.add(i);
            }

            if (i > 0 && rawDataPackage[i] == frameStart308v2 && rawDataPackage[i - 1] == frameEnd308v2) {
                startEndPosFlags.add(i);
            }
        }
    }

    /**
     * 根据有效数据拼接完整的一帧数据。
     *
     * @param sn4Bytes
     *            设备号对应的4字节数组。
     * @param pid
     *            产品号。
     * @param mid
     *            消息号。
     * @param rawDataBytes
     *            有效数据。
     * @return 完整的一帧数据。
     */
    public static byte[] buildFrame308v2NoCrcAndEncode(byte[] sn4Bytes, byte pid, byte mid, byte[] rawDataBytes) {
        long sn = NumberUtil.bytes2Long(sn4Bytes, 0, 4, true);
        return buildFrame308v2NoCrcAndEncode(sn, pid, mid, rawDataBytes);
    }

    /**
     * 根据有效数据拼接完整的一帧数据。
     *
     * @param sn
     *            设备号。
     * @param pid
     *            产品号。
     * @param mid
     *            消息号。
     * @param rawDataBytes
     *            有效数据。
     * @return 完整的一帧数据。
     */
    public static byte[] buildFrame308v2NoCrcAndEncode(long sn, byte pid, byte mid, byte[] rawDataBytes) {
        // 13为除有效数据之外的内容：帧头+包头+帧尾。
        byte[] frameBytes = new byte[rawDataBytes.length + auxDataLength];

        // 帧头。
        frameBytes[0] = frameStart308v2;

        // 帧长度。
        int frameLength = frameBytes.length;
        byte[] frameLengthBytes = NumberUtil.int2Bytes4(frameLength, true);
        frameBytes[1] = frameLengthBytes[2];
        frameBytes[2] = frameLengthBytes[3];

        // 序列号。
        byte[] snBytes = NumberUtil.long2Bytes8(sn, true);
        frameBytes[3] = snBytes[4];
        frameBytes[4] = snBytes[5];
        frameBytes[5] = snBytes[6];
        frameBytes[6] = snBytes[7];

        // 产品id。
        frameBytes[7] = pid;

        // 消息id。
        frameBytes[8] = mid;

        // 有效数据长度（数据包长度）
        byte[] dataLengthBytes = NumberUtil.int2Bytes4(rawDataBytes.length, true);
        frameBytes[9] = dataLengthBytes[2];
        frameBytes[10] = dataLengthBytes[3];

        // 有效数据（载荷）从11开始，前11是帧头和包头的内容。
        System.arraycopy(rawDataBytes, 0, frameBytes, 11, rawDataBytes.length);

        // crc暂时不处理，默认为0。

        // 帧尾。
        frameBytes[frameBytes.length - 1] = frameEnd308v2;

        return frameBytes;
    }

    /**
     * 根据有效数据拼接完整的一帧数据。
     *
     * @param sn4Bytes
     *            设备号对应的4字节数组。
     * @param pid
     *            产品号。
     * @param mid
     *            消息号。
     * @param rawDataBytes
     *            有效数据。
     * @return 完整的一帧数据。
     */
    public static byte[] buildFrame308v2WithCrcAndEncode(byte[] sn4Bytes, byte pid, byte mid, byte[] rawDataBytes) {
        long sn = NumberUtil.bytes2Long(sn4Bytes, 0, 4, true);
        return buildFrame308v2WithCrcAndEncode(sn, pid, mid, rawDataBytes);
    }

    /**
     * 根据有效数据拼接完整的一帧数据。
     *
     * @param sn
     *            设备号。
     * @param pid
     *            产品号。
     * @param mid
     *            消息号。
     * @param rawDataBytes
     *            有效数据。
     * @return 完整的一帧数据。
     */
    public static byte[] buildFrame308v2WithCrcAndEncode(long sn, byte pid, byte mid, byte[] rawDataBytes) {
        byte[] rawFrameBytes = buildFrame308v2NoCrcAndEncode(sn, pid, mid, rawDataBytes);
        return buildFrame308v2WithCrcAndEncode(Long.toString(sn), rawFrameBytes);
    }

    /**
     * 将原始数据，更新设备号和长度之后，重新做crc校验后的完整的一帧数据。
     *
     * @param sn
     *            设备号。
     * @param originFrameBytes
     *            原始数据。
     * @return 更新完设备号和长度之后，完整的一帧数据。
     */
    public static byte[] buildFrame308v2WithCrcAndEncode(String sn, byte[] originFrameBytes) {
        long snLong = Long.parseLong(sn);

        byte[] bytesAfterSetSnAndLength = setSnAndLength308v2(snLong, originFrameBytes);

        return crcAndEncode308v2(bytesAfterSetSnAndLength);
    }

    /**
     * 将已有的数据进行crc校验，然后编码。
     *
     * @param rawFrameBytes
     *            待进行crc校验和编码的数据。
     * @return crc校验和编码之后的数据。
     */
    public static byte[] crcAndEncode308v2(byte[] rawFrameBytes) {
        // crc不包括crc本身和帧尾，减去2个字节，其余都包括。
        byte[] crcDataBytes = new byte[rawFrameBytes.length - 2];
        System.arraycopy(rawFrameBytes, 0, crcDataBytes, 0, rawFrameBytes.length - 2);

        // 完成crc校验。
        byte crcByte = calcCrc8(crcDataBytes);
        rawFrameBytes[rawFrameBytes.length - 2] = crcByte;

        // 完成转码操作。
        return encode308v2(rawFrameBytes);
    }

    /**
     * 对数据进行解码操作，如遇0xFF 0xFF，转码为0xFF；如遇0xFF 0x00，转码为0x55。
     *
     * @param encodeFrameBytes
     *            要解码的数据。
     * @return 解码之后的数据。
     */
    public static byte[] decode308v2(byte[] encodeFrameBytes) {
        List<Byte> decodeList = new ArrayList<>();

        for (int i = 0; i < encodeFrameBytes.length; i++) {
            if (encodeFrameBytes[i] != frameByteFF) {
                decodeList.add(encodeFrameBytes[i]);
                continue;
            }

            if (encodeFrameBytes.length == i + 1) {
                break;
            }

            // 流程走到此处，表示存在0xFF的字节，判断是否遇到0x00或0xFF，遇到这两个需要解码。
            byte nextByte = encodeFrameBytes[i + 1];
            if (nextByte == frameByte00) {
                decodeList.add((byte)0x55);
                i += 1;
                continue;
            }

            if (nextByte == frameByteFF) {
                decodeList.add((byte)0xFF);
                i += 1;
                continue;
            }

            decodeList.add(nextByte);
        }

        byte[] decodeFramebytes = new byte[decodeList.size()];
        for (int i = 0; i < decodeList.size(); i++) {
            decodeFramebytes[i] = decodeList.get(i);
        }

        return decodeFramebytes;
    }

    /**
     * 对数据进行编码操作，如遇0x55，转码为0xFF 0x00；如遇0xFF，转码为0xFF 0xFF。
     *
     * @param rawFrameBytes
     *            要编码的数据。
     * @return 编码后的数据。
     */
    public static byte[] encode308v2(byte[] rawFrameBytes) {
        List<Byte> encodeList = new ArrayList<>();

        for (int i = 0; i < rawFrameBytes.length; i++) {
            if (rawFrameBytes[i] == frameStart308v2 && i > 0) {
                encodeList.add(ff00For55[0]);
                encodeList.add(ff00For55[1]);
                continue;
            }

            if (rawFrameBytes[i] == frameByteFF) {
                encodeList.add(ffffForFF[0]);
                encodeList.add(ffffForFF[1]);
                continue;
            }

            encodeList.add(rawFrameBytes[i]);
        }

        byte[] encodeFrameBytes = new byte[encodeList.size()];
        for (int i = 0; i < encodeList.size(); i++) {
            encodeFrameBytes[i] = encodeList.get(i);
        }

        return encodeFrameBytes;
    }

    /**
     * 将sn和length更新到原始字节数组中。
     *
     * @param sn
     *            设备号。
     * @param originFrameBytes
     *            原始字节数组。
     * @return 更新后的字节数组。
     */
    private static byte[] setSnAndLength308v2(long sn, byte[] originFrameBytes) {
        byte[] snBytes = NumberUtil.long2Bytes8(sn, true);
        System.arraycopy(snBytes, 4, originFrameBytes, 3, 4);

        byte[] frameLengthBytes = NumberUtil.long2Bytes8(originFrameBytes.length, true);
        System.arraycopy(frameLengthBytes, 6, originFrameBytes, 1, 2);

        return originFrameBytes;
    }
}
