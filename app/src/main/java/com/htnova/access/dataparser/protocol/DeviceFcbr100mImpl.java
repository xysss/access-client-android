package com.htnova.access.dataparser.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.dataparser.utils.ProtocolUtil;
import com.htnova.access.pojo.dto.DeviceConfig;
import com.htnova.access.pojo.dto.HeartBeatDataDto;
import com.htnova.access.pojo.dto.InstructDataDto;
import com.htnova.access.pojo.dto.VersionDataDto;

/** 308v2协议解析实现，根据传递的字节数组，获取解析后的数据对象，见协议文档《HT-FCBR-100-通信协议.doc》。 */
class DeviceFcbr100mImpl extends AbstractDeviceProt {
    // 该方法采用的解析规则是按照大端机的方式解析，小端机不支持。
    @Override
    protected byte[] buildReq(byte msgId, String deviceType, String sn, Map<String, Object> params) {
        // 生成请求帧结构：设备号以4个-1表示。
        // 0x00-发送心跳请求，设备收到后会持续发送数据。
        if (msgId == 0x00) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x00, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x02-请求设备基本信息。
        if (msgId == 0x02) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x02, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x4E-请求通知时间间隔。
        if (msgId == 0x4E) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x4E, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x52-请求设备时间。
        if (msgId == 0x52) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x52, new byte[] {0});
            return crcAndEncodeBytes;
        }

        // 0x56-请求传感器配置信息。
        if (msgId == 0x56) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x56, new byte[] {0});
            return crcAndEncodeBytes;
        }

        // 0x5C-请求传感器报警阈值信息。
        if (msgId == 0x5C) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x5C, new byte[] {0x00, (byte)0xFF});
            return crcAndEncodeBytes;
        }

        // 0x60-请求传感器报警阈值场景缺省值信息。
        if (msgId == 0x60) {
            // 场景代码：0-工作场景，1-公共场景。
            byte sceneType = (byte)params.get("sceneType");
            byte[] crcAndEncodeBytes = ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x60,
                new byte[] {0x00, (byte)0xFF, sceneType});
            return crcAndEncodeBytes;
        }

        // 0x66-请求IP地址信息。
        if (msgId == 0x66) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x66, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x6A-请求网络服务信息。
        if (msgId == 0x6A) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x6A, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x6E-请求设备场景信息。
        if (msgId == 0x6E) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x6E, new byte[0]);
            return crcAndEncodeBytes;
        }

        // 0x78-请求模块开关状态。
        if (msgId == 0x78) {
            byte[] crcAndEncodeBytes =
                ProtocolUtil.buildFrame308v2WithCrcAndEncode(0, (byte)0x05, (byte)0x78, new byte[0]);
            return crcAndEncodeBytes;
        }

        return null;
    }

    /**
     * Klv结构是否是大端：默认是大端，部分设备型号为小端。
     *
     * @return 大端返回true，小端返回false。
     */
    @Override
    protected boolean isKlvBigEndian() {
        return false;
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
        }

        byte msgId = data[8];
        if (0x41 == msgId) {
            AbstractDevice protDeviceData = handleKlvData(deviceType, sn1, msgId, data, true);

            if (protDeviceData != null) {
                protDeviceData.setSn(sn1);
            }

            return protDeviceData;
        }

        // 0x01-心跳响应
        // 收到回执后，会根据配置情况确定是否需要向设备发送获取配置的信息。
        if (0x01 == msgId) {
            return handleHeartBeatRes(deviceType, sn1, data);
        }

        // 0x03-设备基本信息（含版本）响应
        if (0x03 == msgId) {
            return handleVersionRes(deviceType, sn1, data);
        }

        // 0x4D-设置通知时间间隔响应。
        // 0x4F-设置HT灵敏度响应（FCBR-100M：获取通知时间间隔响应）。
        // 0x51-设置滤毒罐状态响应（FCBR-100M：设置设备时间响应）。
        // 0x53-设置生物采样器状态响应（FCBR-100M，获取设备时间响应）。
        // 0x57-获取传感器配置响应。
        // 0x59-设置模块开关状态响应。
        // 0x5D-获取传感器报警阈值响应。
        // 0x5F-设置传感器报警阈值响应。
        // 0x61-获取传感器报警阈值场景缺省值响应。
        // 0x67-获取IP地址响应。
        // 0x6B-获取网络服务参数响应。
        // 0x6F-获取设备场景响应。
        // 0x71-设置设备场景响应。
        // 0x79-获取模块开关状态响应。
        if (ProtocolService.isFcbrInstruct(msgId)) {
            InstructDataDto deviceRawData = new InstructDataDto();
            deviceRawData.setDeviceType(deviceType);
            deviceRawData.setSn(sn1);

            // 11个字节协议头，共N个槽位的传感器信息。
            int startPos = 11;

            // 0x4F-设置HT灵敏度响应（FCBR-100M：获取通知时间间隔响应）。
            if (0x4F == msgId) {
                // 暂不处理。
            }

            // 0x53-获取设备时间响应。
            if (0x53 == msgId) {
                Fcbr100mDataResolve.getFcbr100mTimeConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x57-获取传感器配置响应。
            if (0x57 == msgId) {
                Fcbr100mDataResolve.getFcbr100mSensorConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x5D-获取传感器报警阈值响应。
            if (0x5D == msgId) {
                Fcbr100mDataResolve.getFcbr100mAlarmConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x61-获取传感器报警阈值场景缺省值响应。
            if (0x61 == msgId) {
                Fcbr100mDataResolve.getFcbr100mAlarmSceneConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x67-获取IP地址响应。
            if (0x67 == msgId) {
                Fcbr100mDataResolve.getFcbr100mIpAddrConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x6B-获取网络服务参数响应。
            if (0x6B == msgId) {
                Fcbr100mDataResolve.getFcbr100mNetServConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x6F-获取设备场景响应。
            if (0x6F == msgId) {
                Fcbr100mDataResolve.getFcbr100mSceneTypeConfig(startPos, deviceRawData, data, msgId);
            }

            // 0x79-获取模块开关状态响应。
            if (0x79 == msgId) {
                Fcbr100mDataResolve.getFcbr100mModCtrlStateConfig(startPos, deviceRawData, data, msgId);
            }

            // 设置之前先将初始状态加入缓存，获取结果之后，将状态加入缓存，然后返回前清除缓存。
            // 0x4D-设置通知时间间隔响应。
            // 0x51-设置滤毒罐状态响应（FCBR-100M：设置设备时间响应）。
            // 0x59-设置模块开关状态响应。
            // 0x5F-设置传感器报警阈值响应。
            // 0x71-设置设备场景响应。
            if (0x4D == msgId || 0x51 == msgId || 0x59 == msgId || 0x5F == msgId || 0x71 == msgId) {
                byte resultValue = data[startPos];
                boolean successFlag = resultValue == 0 ? true : false;
                ProtocolService.setLastInstructResult(deviceType, sn, msgId, successFlag);
            }

            return handleInstructRes(deviceType, sn1, data);
        }
        return null;
    }

    /** 通过对有效数据解析获取RawData对象，把有效数据设置到对象中，该方法采用的解析规则是按照大端机的方式解析，小端机不支持。 */
    @Override
    protected void handleKlvFrame(AbstractDevice deviceRawData, byte[] effectiveData, int msgId) throws Exception {
        switch (msgId) {
            case 0x26:
                tvocProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 3);
                return;
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
                smokeProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 1);
                return;
            case 0x64:
            case 0x65:
            case 0x66:
            case 0x67:
            case 0x68:
            case 0x69:
            case 0x6A:
            case (byte)0x81:
                sysProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 1);
                return;
            case 0x71:
                weatherProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 0);
                return;
            // 解析过来是-111：Java表达的byte范围与c语言的范围不一致，超过范围时不能以int表达，强制转为byte类型。
            case (byte)0x91:
                cwaProt.protAnalysis(deviceRawData, effectiveData, msgId, AbstractModProt.PROT_VER2, 2);
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

        // 主板嵌入式：
        // Byte 0: fw major version – main board
        // Byte 1: fw minor version – main board
        // VOC嵌入式：
        // Byte 2: fw major version – voc board
        // Byte 3: fw minor version – voc board
        // 主板硬件版本：
        // Byte 4: hw version – main board
        // Byte 5: hw version – voc board
        // double mainVersion = getVersion(data[11], data[12]);
        // double vocVersion = getVersion(data[13], data[14]);
        // int hwMainVersion = NumberUtil.bytes2Int(new byte[] {0x00, data[15]}, true);
        // int hwVocVersion = NumberUtil.bytes2Int(new byte[] {0x00, data[16]}, true);

        // Byte0：主板硬件主版本号，uint8_t
        // Byte1：主板硬件次版本号，uint8_t
        // Byte2：主板软件主版本号，uint8_t
        // Byte3：主板软件次版本号，uint8_t
        //
        // Byte4：TIC模块硬件主版本号，uint8_t
        // Byte5：TIC模块硬件次版本号，uint8_t
        // Byte6：TIC模块软件主版本号，uint8_t
        // Byte7：TIC模块软件次版本号，uint8_t
        //
        // Byte8：CWA模块硬件主版本号，uint8_t
        // Byte9：CWA模块硬件次版本号，uint8_t
        // Byte10：CWA模块软件主版本号，uint8_t
        // Byte11：CWA模块软件次版本号，uint8_t
        //
        // Byte12-31：设备序列号，字符串，20字节
        double hwMainVersion = getVersion(data[11], data[12]);
        double swMainVersion = getVersion(data[13], data[14]);
        double hwTicVersion = getVersion(data[15], data[16]);
        double swTicVersion = getVersion(data[17], data[18]);
        double hwCwaVersion = getVersion(data[19], data[20]);
        double swCwaVersion = getVersion(data[21], data[22]);

        deviceRawData.setHwMainVer(hwMainVersion);
        deviceRawData.setMainVer(swMainVersion);
        deviceRawData.setHwTicVer(hwTicVersion);
        deviceRawData.setTicVer(swTicVersion);
        deviceRawData.setHwCwaVer(hwCwaVersion);
        deviceRawData.setCwaVer(swCwaVersion);

        byte[] destArr = new byte[20];
        System.arraycopy(data, 23, destArr, 0, 20);
        String deviceSn = new String(destArr, StandardCharsets.UTF_8);
        if (deviceSn != null && deviceSn.length() > 0) {
            deviceSn = deviceSn.replaceAll("[\u0000]", "");
        }

        // 设备的基本信息存放到配置中，在外部可以通过ProtocolService获取。
        DeviceConfig deviceConfig = HisDeviceConfig.getConfig(deviceType, sn);
        if (deviceConfig != null) {
            deviceConfig.setHwVer(String.valueOf(hwMainVersion));
            deviceConfig.setSwVer(String.valueOf(swMainVersion));
            deviceConfig.setMainHwVer(String.valueOf(hwMainVersion));
            deviceConfig.setMainSwVer(String.valueOf(swMainVersion));
            deviceConfig.setTicHwVer(String.valueOf(hwTicVersion));
            deviceConfig.setTicSwVer(String.valueOf(swTicVersion));
            deviceConfig.setCwaHwVer(String.valueOf(hwCwaVersion));
            deviceConfig.setCwaSwVer(String.valueOf(swCwaVersion));
            deviceConfig.setDeviceSn(deviceSn);
        }

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
