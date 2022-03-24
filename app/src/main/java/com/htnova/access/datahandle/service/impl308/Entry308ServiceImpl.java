package com.htnova.access.datahandle.service.impl308;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.htnova.access.commons.utils.NumberUtil;
import com.htnova.access.datahandle.pojo.ResultDto;
import com.htnova.access.datahandle.service.EntryService;
import com.htnova.access.datahandle.service.ParserService;
import com.htnova.access.datahandle.service.impl.EntryBasicServiceImpl;
import com.htnova.access.dataparser.protocol.ProtocolService;
import com.htnova.access.dataparser.utils.ProtocolUtil;
import com.htnova.access.pojo.dto.*;
import com.htnova.access.socket.DeviceControlService;
import com.htnova.access.sysconfig.constdef.ModDef;
import com.htnova.access.sysconfig.constdef.ProductDef;
import com.htnova.access.socket.Fcbr100mService;

/** 308Local设备接入入口实现。 */
public class Entry308ServiceImpl extends EntryBasicServiceImpl implements EntryService {
    private ParserService dataParseService = new Parser308ServiceImpl();

    private Fcbr100mService fcbr100mService = Fcbr100mService.getInstance();

    private DeviceControlService deviceControlService = DeviceControlService.getInstance();

    /**
     * 308v2设备入口数据处理的切入点，未经处理，直接从设备发送的数据。 数据包中可能包含多帧协议的数据，打成一个包进行发送。
     *
     * @param dataPackage
     *            设备原始数据包，可能包含多帧协议数据。
     * @param productCode
     *            产品代码，如0x05。
     * @param deviceType
     *            设备类型，如FCBR-100、FCBR-100B。
     * @param sn
     *            消息队列，旧版配置无法解析出设备号，如HT308PRD/308/C2S/，新版配置可以解析出设备号，如HT308PRD/FCBR-100/C2S/2004020001/。
     * @param deviceSeq
     *            有些设备由多台数据组合而成，如HT308PRD/CIAE-RMS-C/C2S/2006060001/01/。
     * @return BaseInfoVO类型的数据。
     */
    @Override
    public ResultDto handleEntryData(byte[] dataPackage, int productCode, String deviceType, String sn,
                                     String deviceSeq) {
        // 新设备接入sn或deviceType至少有一个不为空。
        // 通过MQTT接入，sn不为空，然后可以查询到deviceType。
        // 通过Socket接入，deviceType不为空，严格为不同的设备型号分配独立的socket监听端口。
        // 基于以上的原因，deviceType一定不为空。因此，在协议解析时，可以通过deviceType来区分进行解析。

        // 根据原始设备数据，分拆出数据帧列表，一个数据包中可能包含多帧协议数据。
        List<byte[]> dataFrames = ProtocolUtil.getFrames308v2(dataPackage);

        // 针对每一帧数据分别进行处理。
        for (byte[] dataFrame : dataFrames) {
            // FCBR-100CP用到了GM606，GM606配置不固定，需要在启动时，发送获取配置的消息。
            if (isNeedReGetConfig(deviceType, sn)) {
                // FCBR-100M需要发送的指令很多，单独调用。其余设备只发送获取配置即可。
                if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
                    fcbr100mService.sendDeviceInfoReq(deviceType, sn, false);
                } else {
                    if (!ProtocolService.containsConfig(deviceType, sn)) {
                        byte[] configReq = ProtocolService.buildReq((byte)0x56, deviceType, sn, null);
                        deviceControlService.sendToDevice(deviceType, sn, configReq, 0);
                        log.info("发送获取配置指令完成，deviceType={}，sn={}", deviceType, sn);
                    }
                }
            }

            handleEntryData(dataParseService, "Entry308v2", dataFrame, productCode, deviceType, sn, deviceSeq, true);
        }

        return null;
    }

    @Override
    protected void afterParseRawData(DeviceDataDto deviceDataDto, String sn, String deviceSeq) {
        if (deviceDataDto == null) {
            return;
        }

        ModCwaDto modCwaDto = deviceDataDto.getCwaMod();
        if (modCwaDto != null) {
            modCwaDto.setSccellData(
                NumberUtil.getFloatPrecise((modCwaDto.getSccell1Data() + modCwaDto.getSccell2Data()) / 2, 6));
            modCwaDto.setSccellRData(
                NumberUtil.getFloatPrecise((modCwaDto.getSccell1RData() + modCwaDto.getSccell2RData()) / 2, 6));
        }
    }

    /**
     * 添加本底数值 smokeBground，pidBground，nh3Bground，cl2Bground，coBground bioEffectiveBground
     *
     * @param deviceDataDto
     */
    @Override
    protected void afterParseDeviceData(DeviceDataDto deviceDataDto) {
        // 此处根据设备型号，去掉不包含的模块：用以模拟数据时，剔除多余的模块。
        String deviceType = deviceDataDto.getDeviceType();
        // 判断模块数据 是否为null，如果为null，则初始化为空数据
        deviceDataDto.initEmptyMods(new String[] {ModDef.TYPE_BIO, ModDef.TYPE_CWA});
        // 根据设备类型不同，去掉没有的模块
        switch (deviceType) {
            case ProductDef.DEVICE_TYPE_FCBR100:
            case ProductDef.DEVICE_TYPE_FCBR100F:
                deviceDataDto.clearMods(
                    new String[] {ModDef.TYPE_NUCLEAR, ModDef.TYPE_WEATHER, ModDef.TYPE_GCAMERA, ModDef.TYPE_EC});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100PRO:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_EC, ModDef.TYPE_GCAMERA});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100CP:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_BIO, ModDef.TYPE_NUCLEAR, ModDef.TYPE_GCAMERA});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100B:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_TVOC, ModDef.TYPE_CWA, ModDef.TYPE_NUCLEAR,
                    ModDef.TYPE_GCAMERA, ModDef.TYPE_EC, ModDef.TYPE_WEATHER});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100G:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_WEATHER, ModDef.TYPE_EC});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100C:
            case ProductDef.DEVICE_TYPE_FCBR100M:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_BIO, ModDef.TYPE_NUCLEAR, ModDef.TYPE_WEATHER,
                    ModDef.TYPE_GCAMERA, ModDef.TYPE_EC});
                break;
            case ProductDef.DEVICE_TYPE_FCBR100S1:
                deviceDataDto.clearMods(new String[] {ModDef.TYPE_SMOKE, ModDef.TYPE_BIO, ModDef.TYPE_TVOC,
                    ModDef.TYPE_WEATHER, ModDef.TYPE_NUCLEAR, ModDef.TYPE_GCAMERA, ModDef.TYPE_SYS});
                break;
            default:
                break;
        }

        // FCBR-100M需要烟感的报警结果，然后对传感器进行组合，填充配置信息。
        if (ProductDef.DEVICE_TYPE_FCBR100M.equals(deviceType)) {
            fcbr100mService.reArrangeFcbr100mData(deviceDataDto);
        }
    }

    /**
     * <pre>
     *     记录最近一次获取到数据的时间，以判断是否需要清理配置和发送获取配置的指令。
     *     设定两次发送获取配置指令之间的间隔不能小于20秒，否则太频繁，容易收到太多的消息，导致包之间混合，不好解析。
     *     将配置是否过期需要清除的判断也放到这里进行。
     * </pre>
     */
    private static final Map<String, Long> lastReceiveTime = new ConcurrentHashMap<>();
    private static long configNotExistsInterval = 20000;
    private static long configTimeout = 120000;

    private boolean isNeedReGetConfig(String deviceType, String sn) {
        if (deviceType == null || sn == null) {
            return false;
        }

        if (lastReceiveTime.containsKey(sn)) {
            Long lastMillis = lastReceiveTime.get(sn);
            Long currMillis = System.currentTimeMillis();

            // 很长时间未收到数据，可能是设备刚开机，需要清理配置，然后重新获取配置。
            if ((currMillis - lastMillis) > configTimeout) {
                ProtocolService.clearConfig(deviceType, sn);
            }

            // 避免短时间内重复发送获取配置的指令。
            if ((currMillis - lastMillis) < configNotExistsInterval) {
                return false;
            }
        }

        // 重新开始计时。
        lastReceiveTime.put(sn, System.currentTimeMillis());

        return true;
    }
}
