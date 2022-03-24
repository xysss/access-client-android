package com.htnova.access.pojo.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.htnova.access.commons.pojo.AbstractParam;
import com.htnova.access.commons.utils.StringUtil;
import com.htnova.access.pojo.po.ParamItem;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 前端用设备参数数据（VO）。 前端设备参数数据由数据单元构成，都包括分为高中低三部分，当前使用的参数通过paramLevel来标识。
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Slf4j
public class DeviceParamVo extends AbstractParam implements Cloneable {
    // 此处转化为Map结构，适应前端参数映射的变化。
    private Map<String, String> lowLevel = new HashMap<>();
    private Map<String, String> midLevel = new HashMap<>();
    private Map<String, String> highLevel = new HashMap<>();

    public DeviceParamVo() {
        super();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean containsItem(String key) {
        if (paramLevel == LEVEL_LOW) {
            return lowLevel.containsKey(key);
        }
        if (paramLevel == LEVEL_MID) {
            return midLevel.containsKey(key);
        }
        if (paramLevel == LEVEL_HIGH) {
            return highLevel.containsKey(key);
        }
        return false;
    }

    public void putItemToMap(String key, String value, int level) {
        if (level == LEVEL_LOW) {
            lowLevel.put(key, value);
            return;
        }
        if (level == LEVEL_MID) {
            midLevel.put(key, value);
            return;
        }
        if (level == LEVEL_HIGH) {
            highLevel.put(key, value);
            return;
        }
    }

    public List<ParamItem> mapToItems() {
        List<ParamItem> items = new ArrayList<>();
        lowLevel.forEach((tempKey, tempValue) -> {
            ParamItem item = new ParamItem();
            item.setCode(tempKey);
            item.setLowValue(StringUtil.toString(tempValue, "0"));
            item.setMidValue(StringUtil.toString(midLevel.get(tempKey), "0"));
            item.setHighValue(StringUtil.toString(highLevel.get(tempKey), "0"));
            items.add(item);
        });
        return items;
    }

    public int getIntItemValue(String item) {
        String value = getStringItemValue(item);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.error("sn：{}，参数项目：{}，参数值：{}，转换为int时出现异常，直接返回默认值0。", this.sn, item, value, e);
            return 0;
        }
    }

    public float getFloatItemValue(String item) {
        String value = getStringItemValue(item);
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            log.error("sn：{}，参数项目：{}，参数值：{}，转换为float时出现异常，直接返回默认值0", this.sn, item, value, e);
            return 0f;
        }
    }

    public double getDoubleItemValue(String item) {
        String value = getStringItemValue(item);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            log.error("sn：{}，参数项目：{}，参数值：{}，转换为double时出现异常，直接返回默认值0", this.sn, item, value, e);
            return 0d;
        }
    }

    public String getStringItemValue(String item) {
        String value = null;
        if (paramLevel == LEVEL_LOW) {
            value = lowLevel.get(item);
        }
        if (paramLevel == LEVEL_MID) {
            value = midLevel.get(item);
        }
        if (paramLevel == LEVEL_HIGH) {
            value = highLevel.get(item);
        }

        if (value == null) {
            log.error("sn：{}，参数项目：{}，未配置，直接返回null", this.sn, item);
            return null;
        }

        return value;
    }

    public String getStringItemValue(String item, int level, String defaultValue) {
        String value = null;
        if (level == LEVEL_LOW) {
            value = lowLevel.get(item);
        }
        if (level == LEVEL_MID) {
            value = midLevel.get(item);
        }
        if (level == LEVEL_HIGH) {
            value = highLevel.get(item);
        }

        if (value == null) {
            log.error("sn：{}，参数项目：{}，未配置，直接返回null", this.sn, item);
            return defaultValue;
        }

        return value;
    }
}
