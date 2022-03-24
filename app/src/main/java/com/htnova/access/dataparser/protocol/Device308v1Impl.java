package com.htnova.access.dataparser.protocol;

import java.util.ArrayList;
import java.util.List;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;

/** 308v1协议解析实现，根据传递的字节数组，获取解析后的数据对象。 */
class Device308v1Impl extends AbstractDeviceProt {
    // 协议总开头。
    private static byte[] pkgHead = new byte[] {0x40, 0x00, 0x05};

    // 协议内部每一帧数据结尾，不是总结尾。
    private static byte frameEnd = 0x46;

    private static byte[] frameHead = new byte[] {0x00, 0x01};

    // 通过上传的全部的字节数据，获取有效数据对象。
    @Override
    protected AbstractDevice analysisProtocol(byte[] deviceData, int productCode, String deviceType, String sn)
        throws Exception {
        if (!checkHeadAndTail(deviceData)) {
            log.error("数据校验不通过，deviceType={}，sn={}，bytes={}", deviceType, sn, NumberUtil.bytesToHexString(deviceData));
            throw new Exception("数据头验证不通过");
        }

        List<byte[]> validFrames = getValidFrames(deviceData);
        AbstractDevice deviceRawData = new DeviceDataDto();

        for (byte[] eachFrame : validFrames) {
            handleDataFrame(deviceRawData, eachFrame);
        }

        return deviceRawData;
    }

    // 通过对有效为数据解析获取Data对象，把有效位数据设置到对象中，该方法采用的解析规则是按照大端机的方式解析，小端机不支持。
    private void handleDataFrame(AbstractDevice deviceRawData, byte[] eachFrame) throws Exception {
        if (eachFrame.length < 5) {
            throw new Exception("数据长度小于5，表示无效数据");
        }
        int length = NumberUtil.byte2Int(eachFrame[3]);
        if (eachFrame.length < 5 + length) {
            throw new Exception("有效位数对应不上，报错");
        }

        byte[] effectiveData = new byte[length];
        System.arraycopy(eachFrame, 4, effectiveData, 0, length);
        if (eachFrame[0] == frameHead[0] || eachFrame[0] == frameHead[1]) {
            byte msgId = eachFrame[1];
            switch (msgId) {
                case 0x01:
                case 0x02:
                case 0x03:
                case 0x04:
                case 0x05:
                case 0x06:
                case 0x07:
                case 0x08:
                case 0x09:
                case 0x0A:
                case 0x0B:
                case 0x0C:
                    cwaProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER1, 0);
                    return;
                case 0x21:
                    tvocProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER1, 0);
                    return;
                case 0x31:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                    smokeProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER1, 0);
                    return;
                case 0x41:
                    bioProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER1,
                        ModBioImpl.BUSITYPE_308);
                    return;
                case 0x51:
                case 0x61:
                    sysProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER1, 0);
                    return;
                default:
                    return;
            }
        }
        return;
    }

    // 对上传的字节码进行解析按照有效位拆分出来。
    private List<byte[]> getValidFrames(byte[] data) throws Exception {
        List<byte[]> list = new ArrayList();

        // 前6个为包头40 00 05 16 01 1B，每一帧数据00 01 00 04 数据1 数据2 数据3 数据4 46长度除有效数据外至少5字节。
        int cursor = 6;
        if (data.length < cursor + 5) {
            throw new Exception("有效数据长度为0");
        }

        while (true) {
            // 每一帧数据以0x00或0x01开头。
            if (0x00 != data[cursor] && 0x01 != data[cursor]) {
                break;
            }

            // 每一帧有3字节帧头，如00 01 00。
            if (data.length <= cursor + 3) {
                break;
            }

            // 找到每帧有效数据的长度。
            int dataLength = NumberUtil.byte2Int(data[cursor + 3]);

            // 每一帧数据00 01 00 04 数据1 数据2 数据3 数据4 46，3字节开头，1字节数据长度，1字节结尾。
            int end = cursor + 3 + 1 + dataLength + 1;
            if (data.length < end || frameEnd != data[end - 1]) {
                break;
            }

            // 将每一帧数据放到列表中。
            byte[] eachFrameBytes = new byte[dataLength + 5];
            System.arraycopy(data, cursor, eachFrameBytes, 0, dataLength + 5);
            list.add(eachFrameBytes);

            // 索引下一帧数据。
            cursor += (5 + dataLength);
        }

        return list;
    }

    // 判断数据的完整性：40 00 05 16 01 1B 包头/22项内容/283总字节长度。
    private boolean checkHeadAndTail(byte[] data) {
        // 判断开头3个字节，40 00 05。
        for (int i = 0; i < pkgHead.length; i++) {
            if (pkgHead[i] != data[i]) {
                return false;
            }
        }

        // 包头长度至少为6，少于6为不合法的包结构。
        if (data.length < 6) {
            return false;
        }

        // data[4]为01，表示总共只有1包数据。
        // 按照32个字节1行排列，完整的有8行，共256字节。剩余的27字节用data[5]表示为1B。
        // 因此凑够了一个简单的计算总和。
        int totalLength = NumberUtil.byte2Int(data[4]) * 256 + NumberUtil.byte2Int(data[5]);
        return totalLength <= data.length;
    }
}
