package com.htnova.access.dataparser.protocol;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.dataparser.utils.ProtocolUtil;
import com.htnova.access.pojo.dto.HeartBeatDataDto;
import com.htnova.access.pojo.dto.InstructDataDto;
import com.htnova.access.pojo.dto.VersionDataDto;

/** 308v2协议解析实现，根据传递的字节数组，获取解析后的数据对象，见协议文档《HT-FCBR-100-通信协议.doc》。 */
class Device308v2Impl extends AbstractDeviceProt {
    // 该方法采用的解析规则是按照大端机的方式解析，小端机不支持。
    @Override
    protected byte[] buildReq(byte msgId, String deviceType, String sn, Map<String, Object> params) {
        // 生成请求帧结构：0x56-请求配置信息。
        if (msgId == 0x56) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(Long.parseLong(sn), (byte)0x05, (byte)0x56, new byte[0]);
            return crcAndEncodeBytes;
        }
        return null;
    }

    @Override
    protected AbstractDevice analysisProtocol(byte[] deviceData, int productCode, String deviceType, String sn)
        throws Exception {
        // 308v2协议遇到开头0x55和0xFF需要编码，因此，使用时要先解码。
        byte[] data = ProtocolUtil.decode308v2(deviceData);
        if (!ProtocolUtil.isValid308v2Frame(data)) {
            log.error("数据校验不通过，deviceType={}，sn={}，decryptBytes={}", deviceType, sn, NumberUtil.bytesToHexString(data));
            throw new Exception("数据验证不通过");
        }

        // 获取设备序列号。
        long snLong;
        String sn1;

        // 如果接口参数中已经传入sn号，则使用该sn号，不需要从数据中解析
        if (!StringUtils.isBlank(sn)) {
            // sn号可能是0或非数字开头，转换可能会出错或不一致。
            // snLong = Long.parseLong(sn);
            sn1 = sn;
        } else {
            snLong = NumberUtil.bytes2Long(new byte[] {data[3], data[4], data[5], data[6]}, true);
            sn1 = Long.toString(snLong);

            // 2021-05-04：消博会，2004020059的序列号突然变为0，临时处理。
            if (snLong == 0L || snLong == 4294967295L) {
                snLong = 2004020059;
                sn1 = "2004020059";
            }
        }

        byte msgId = data[8];
        if (0x41 == msgId) {
            AbstractDevice protDeviceData = handleKlvData(deviceType, sn1, msgId, data, true);

            // 2021-05-04：消博会，2004020059的序列号突然变为0，临时处理。
            if (protDeviceData != null) {
                protDeviceData.setSn(sn1);
            }
            return protDeviceData;
        }

        // 0x01-心跳响应
        if (0x01 == msgId) {
            return handleHeartBeatRes(deviceType, sn1, data);
        }

        // 0x03-版本响应
        if (0x03 == msgId) {
            return handleVersionRes(deviceType, sn1, data);
        }

        // 0x05-0x57-其它响应
        if (ProtocolService.isFcbrInstruct(msgId)) {
            // 0x57-电化学2的数据读取响应（电化学2模块的配置信息）。
            if (0x57 == msgId) {
                InstructDataDto deviceRawData = new InstructDataDto();
                deviceRawData.setDeviceType(deviceType);
                deviceRawData.setSn(sn1);

                // 0-27先不解析，从28开始，加上11个字节协议头，从39开始，共6个槽位的传感器信息。
                int startPos = 11 + 28;
                Gd606DataResolve.getGd606SensorConfig(startPos, deviceRawData, data, msgId);
            }
            return handleInstructRes(deviceType, sn1, data);
        }
        return null;
    }

    /** 通过对有效数据解析获取RawData对象，把有效数据设置到对象中，该方法采用的解析规则是按照大端机的方式解析，小端机不支持。 */
    @Override
    protected void handleKlvFrame(AbstractDevice deviceRawData, byte[] effectiveData, int msgId) throws Exception {
        switch (msgId) {
            // sn可能由外部传递过来，此处不解析sn，以免造成不一致。
            // case 0x00:
            // deviceRawData.setSn(Long.toString(NumberUtil.bytes2Long(effectiveData, true)));
            // return;
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
            case 0x0D:
                cwaProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x21:
                tvocProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x24:
                ecProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
                smokeProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x41:
            case 0x42:
            case 0x43:
                bioProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2,
                    ModBioImpl.BUSITYPE_308);
                return;
            case 0x44:
            case 0x51:
            case 0x61:
            case 0x62:
            case 0x63:
                sysProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x71:
                weatherProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            case 0x76:
                nuclearProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            default:
                return;
        }
    }

    /** 处理心跳结果响应。 */
    private AbstractDevice handleHeartBeatRes(String deviceType, String sn, byte[] data) throws Exception {
        HeartBeatDataDto deviceRawData = new HeartBeatDataDto();
        deviceRawData.setHeartBeat(true);
        deviceRawData.setDeviceType(deviceType);
        deviceRawData.setSn(sn);
        deviceRawData.setMsgId(0x01);
        return deviceRawData;
    }

    /** 处理指令结果响应。 */
    private AbstractDevice handleInstructRes(String deviceType, String sn, byte[] data) throws Exception {
        int payloadLength = getPayloadLength(data);
        if (payloadLength < 1) {
            throw new Exception("获取版本号有效字节长度过短");
        }

        int result = NumberUtil.byte2Int(data[11]);

        InstructDataDto deviceRawData = new InstructDataDto();
        deviceRawData.setDeviceType(deviceType);
        deviceRawData.setSn(sn);
        deviceRawData.setMsgId(NumberUtil.bytes2Int(new byte[] {0x00, data[8]}, true));
        deviceRawData.setResult(result);

        return deviceRawData;
    }

    /** 处理版本结果响应。 */
    private AbstractDevice handleVersionRes(String deviceType, String sn, byte[] data) throws Exception {
        int payloadLength = getPayloadLength(data);
        if (payloadLength < 6) {
            throw new Exception("获取版本号有效字节长度过短");
        }

        VersionDataDto deviceRawData = new VersionDataDto();
        deviceRawData.setDeviceType(deviceType);
        deviceRawData.setSn(sn);
        deviceRawData.setMsgId(0x03);

        double mainVersion = getVersion(data[11], data[12]);
        double vocVersion = getVersion(data[13], data[14]);
        int hwMainVersion = NumberUtil.bytes2Int(new byte[] {0x00, data[15]}, true);
        int hwVocVersion = NumberUtil.bytes2Int(new byte[] {0x00, data[16]}, true);

        deviceRawData.setMainVer(mainVersion);
        deviceRawData.setVocVer(vocVersion);
        deviceRawData.setHwMainVer(hwMainVersion);
        deviceRawData.setHwVocVer(hwVocVersion);

        return deviceRawData;
    }

    /** 通过主次字节获取当前版本号。 */
    private double getVersion(byte majorByte, byte minorByte) {
        // 获取当前主版本号
        int majorVersion = NumberUtil.bytes2Int(new byte[] {0x00, majorByte}, true);

        // 获取当前次版本号
        int minorVersion = NumberUtil.bytes2Int(new byte[] {0x00, minorByte}, true);

        String versionString = majorVersion + "." + minorVersion;

        // 获取当前版本号
        return Double.parseDouble(versionString);
    }
}
