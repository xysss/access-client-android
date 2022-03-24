package com.htnova.access.dataparser.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.htnova.access.pojo.dto.DeviceConfig;

/**
 * 设备配置信息工具类。
 */
class HisDeviceConfig {
    // 针对GD606设备，用以保存每台设备不同模块的配置信息，模块由0-5的数字组成。
    // 针对Fcbr100m设备，因为传感器比较多，分为两组，一组0-17（TVOC），另一组0-6（CWA）。
    private static Map<String, DeviceConfig> deviceConfigs = new ConcurrentHashMap<>();

    // 不需要外部构造。
    private HisDeviceConfig() {}

    /**
     * 是否存在设备的配置信息。预留deviceType参数用以后续的扩展。
     * 
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @return 存在返回true，否则返回false。
     */
    static boolean containsConfig(String deviceType, String sn) {
        // sn未知时，不做处理。
        if (sn == null) {
            return true;
        }

        return deviceConfigs.containsKey(sn);
    }

    /**
     * 重新设置设备的配置信息，当前只有GD606和FCBR-100CP需要设置配置信息。
     * 
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     * @param value
     *            要设置的配置信息。
     */
    static void putConfig(String deviceType, String sn, DeviceConfig value) {
        // sn未知时，不做处理。
        if (sn == null) {
            return;
        }

        deviceConfigs.put(sn, value);
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
    static DeviceConfig getConfig(String deviceType, String sn) {
        // sn未知时，不做处理。
        if (sn == null) {
            return null;
        }

        return deviceConfigs.get(sn);
    }

    /**
     * 清除设备配置信息。当设备重新初始化或设备模块有变化时，需要清除配置信息，以便重新获取最新的配置信息。
     * 
     * @param deviceType
     *            设备型号。
     * @param sn
     *            设备号。
     */
    static void clearConfig(String deviceType, String sn) {
        // sn未知时，不做处理。
        if (sn == null) {
            return;
        }

        if (deviceConfigs.containsKey(sn)) {
            deviceConfigs.remove(sn);
        }
    }

    /**
     * 清除所有设备配置信息。
     */
    static void clearAllConfig() {
        deviceConfigs.clear();
    }
}
