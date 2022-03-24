package com.htnova.access.dataparser.protocol;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.htnova.access.commons.pojo.AbstractDevice;
import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.pojo.dto.DeviceDataDto;
import com.htnova.access.sysconfig.constdef.ProductDef;

/**
 * <pre>
 *     设备数据协议解析的抽象父类，定义了协议解析的抽象接口。
 *     针对klv解析协议，提供了模板方法和待扩展实现的方法。
 *     协议文档目录：03-项目文档\HTVision\04-系统设计\通信协议，03-项目文档\HTVision\04-系统设计\第三方产品。
 * </pre>
 */
abstract class AbstractDeviceProt {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected final AbstractModProt smokeProt = new ModSmokeImpl();
    protected final AbstractModProt bioProt = new ModBioImpl();
    protected final AbstractModProt tvocProt = new ModTvocImpl();
    protected final AbstractModProt cwaProt = new ModCwaImpl();
    protected final AbstractModProt sysProt = new ModSysImpl();
    protected final AbstractModProt weatherProt = new ModWeatherImpl();
    protected final AbstractModProt nuclearProt = new ModNuclearImpl();
    protected final AbstractModProt ecProt = new ModEcImpl();

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
    protected abstract AbstractDevice analysisProtocol(byte[] dataFrame, int productCode, String deviceType, String sn)
        throws Exception;

    /**
     * <pre>
     *     只针对支持klv的308v2协议，其它协议不需要实现。
     *     klv是协议内部有效数据的组织方式，一次协议帧的有效数据中，可能包括多组klv，根据klv的k（相当于内部msgId）进行解析。
     *     该方法直接在父类的handleKlvData的模板方法中调用。
     *     根据协议的内容，以msgId号区分协议，进行解析，填充到deviceRawData的相应模块中。
     * </pre>
     *
     * @param deviceRawData
     *            解析后的设备数据，由handleKlvData方法内部创建。
     * @param effectiveData
     *            有效的协议数据，klv的数据内容，不包含kl，只有v。
     * @param msgId
     *            消息号。
     * @throws Exception
     */
    protected void handleKlvFrame(AbstractDevice deviceRawData, byte[] effectiveData, int msgId) throws Exception {}

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
    protected byte[] buildReq(byte msgId, String deviceType, String sn, Map<String, Object> params) {
        return null;
    }

    /**
     * Klv结构是否是大端：默认是大端，部分设备型号为小端。
     *
     * @return 大端返回true，小端返回false。
     */
    protected boolean isKlvBigEndian() {
        return true;
    }

    /**
     * <pre>
     *     KLV数据解析的模板方法，固化了解析流程，由子类重写handleKlvFrame完成解析功能。
     *     解析KLV格式的data数据，是指协议0x41中的内容数据，这里面的数据采用K（key）L（length）V（value）的方式。
     *     klv是协议内部有效数据的组织方式，一次协议帧的有效数据中，可能包括多组klv，根据klv的k（相当于内部msgId）进行解析。
     * </pre>
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param msgId
     *            消息号。
     * @param data
     *            解码后的一帧协议数据。
     * @param hasEncryptBit
     *            数据内容是否加密标识。
     * @return 解析后的设备数据实体。
     * @throws Exception
     */
    AbstractDevice handleKlvData(String deviceType, String sn, int msgId, byte[] data, boolean hasEncryptBit)
        throws Exception {
        // 获取有效载荷的长度。
        int payloadLength = getPayloadLength(data);

        // 根据协议约定，0x41中的内容，第0字节为加密方式，不在klv的范围，因此长度减1。
        byte[] effectiveData;

        if (hasEncryptBit) {
            byte encryptFlag = data[11];
            effectiveData = new byte[payloadLength - 1];
            System.arraycopy(data, 12, effectiveData, 0, payloadLength - 1);

            // 加密：采用固定key的des加密方式。
            if (1 == encryptFlag) {
                // 先解密。
                effectiveData =
                    decrypt(effectiveData, new byte[] {0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte)0b10001000});
            }
        } else {
            effectiveData = new byte[payloadLength];
            System.arraycopy(data, 11, effectiveData, 0, payloadLength);
        }

        AbstractDevice deviceRawData = createDeviceData(deviceType, sn, msgId);

        // 按照klv规则对数据进行拆分，每一个拆分的结果对应0x41协议内部的一组数据。
        List<byte[]> klvFrames = getKlvFrames(effectiveData);

        boolean isKlvBigEndian = isKlvBigEndian();

        for (byte[] eachFrame : klvFrames) {
            // 每组klv长度为至少3，1Key + 2Length。
            if (eachFrame.length < 3) {
                throw new Exception("数据长度小于3，无效数据");
            }

            // 有效数据长度
            int payloadLengthKlv = NumberUtil.bytes2Int(new byte[] {eachFrame[1], eachFrame[2]}, isKlvBigEndian);
            if (eachFrame.length < 3 + payloadLengthKlv) {
                throw new Exception("有效数据对应不上，无效数据");
            }

            // 拷贝有效数据到待处理的数组中。
            byte[] effectiveDataKlv = new byte[payloadLengthKlv];
            if (payloadLengthKlv > 0) {
                System.arraycopy(eachFrame, 3, effectiveDataKlv, 0, eachFrame.length - 3);
            }

            // 子类扩展该方法，实现具体的klv协议解析。
            handleKlvFrame(deviceRawData, effectiveDataKlv, eachFrame[0]);
        }

        // 返回解析后的klv数据列表。
        return deviceRawData;
    }

    /**
     * 在协议解析之前，先创建接收数据的实体对象。
     *
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param msgId
     *            消息号。
     * @return 初始设备数据实体对象。
     */
    AbstractDevice createDeviceData(String deviceType, String sn, int msgId) {
        AbstractDevice deviceRawData = new DeviceDataDto();

        deviceRawData.setDeviceType(deviceType);
        deviceRawData.setSn(sn);
        deviceRawData.setMsgId(msgId);

        return deviceRawData;
    }

    /**
     * 获取v2协议（308v2之后的设备，B02所有设备）的有效数据载荷字节长度。
     *
     * @param data
     *            解码后的一帧协议数据。
     * @return 有效载荷的长度。
     * @throws Exception
     */
    int getPayloadLength(byte[] data) throws Exception {
        // 大端序，低位在高地址。
        byte[] payloadLengthBytes = new byte[] {data[9], data[10]};
        int payloadLength = NumberUtil.bytes2Int(payloadLengthBytes, true);

        // v2协议，最小帧长度（没有数据载荷时）为13，低于13为无效数据帧。
        if (data.length - 13 < payloadLength) {
            throw new Exception("数据有效位数少于实际位数");
        }

        return payloadLength;
    }

    /**
     * 根据字节数组获取设备的SN号。
     *
     * @param snLongArray
     *            设备号字节数组。
     * @param isBigEndian
     *            是否大小端的标志。
     * @return 字符串表示的设备号。
     * @throws Exception
     */
    String getDeviceSn(byte[] snLongArray, boolean isBigEndian) throws Exception {
        long snLong = NumberUtil.bytes2Long(snLongArray, isBigEndian);

        if (snLong <= 0 || snLong >= 3100000000L) {
            throw new Exception("设备序列号为0或超过最大值，sn=" + snLong);
        }

        String sn = Long.toString(snLong);

        return sn;
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
    boolean containsConfig(String deviceType, String sn) {
        // 只针对需要的设备型号，其余设备型号不处理。
        if (ProductDef.DEVICE_TYPE_GD606.equals(deviceType) || ProductDef.DEVICE_TYPE_FCBR100CP.equals(deviceType)
            || ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            return HisDeviceConfig.containsConfig(deviceType, sn);
        }

        return true;
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
    void clearConfig(String deviceType, String sn) {
        // 只针对需要的设备型号，其余设备型号不处理。
        if (ProductDef.DEVICE_TYPE_GD606.equals(deviceType) || ProductDef.DEVICE_TYPE_FCBR100CP.equals(deviceType)
            || ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            if (HisDeviceConfig.containsConfig(deviceType, sn)) {
                HisDeviceConfig.clearConfig(deviceType, sn);
            }
        }
    }

    /**
     * <pre>
     *     从协议数据中，解析出一组组klv数据，通过列表返回。
     * </pre>
     *
     * @param data
     *            协议数据。
     * @return klv数据列表。
     */
    private List<byte[]> getKlvFrames(byte[] data) {
        // 按照klv规则对数据进行拆分，每一个拆分的结果对应0x41协议内部的一组数据。
        List<byte[]> list = new ArrayList<>();

        boolean isKlvBigEndian = isKlvBigEndian();

        // 每组klv长度为至少3，1Key + 2Length + 0Value。
        int cursor = 0;
        while (true) {
            if (data.length < (cursor + 3)) {
                break;
            }

            int dataLength = NumberUtil.bytes2Int(new byte[] {data[cursor + 1], data[cursor + 2]}, isKlvBigEndian);
            if (data.length < (cursor + dataLength + 3)) {
                break;
            }

            byte[] bytes = new byte[dataLength + 3];
            System.arraycopy(data, cursor, bytes, 0, dataLength + 3);
            list.add(bytes);

            cursor += (dataLength + 3);
        }

        return list;
    }

    /**
     * 对加密字符串进行DES解密。
     *
     * @param input
     *            输入字符串。
     * @param key
     *            秘钥。
     * @return 解密后的字符串，
     * @throws Exception
     */
    private byte[] decrypt(byte[] input, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源。
        SecureRandom random = new SecureRandom();

        // 创建一个DESKeySpec对象。
        DESKeySpec desKey = new DESKeySpec(key);

        // 创建一个密匙工厂。
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        // 将DESKeySpec对象转换成SecretKey对象。
        SecretKey securekey = keyFactory.generateSecret(desKey);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");

        // 用密匙初始化Cipher对象。
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);

        // 真正开始解密操作。
        return cipher.doFinal(input);
    }
}
