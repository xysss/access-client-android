package com.htnova.access.commons.pojo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 设备模块的高层抽象，设备模块相关的实体都需要继承该抽象类。
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class AbstractMod implements Cloneable, Serializable {
    protected String modType; // 模块类型，每个模块的英文唯一标识。

    @Builder.Default
    protected int state = 0; // 模块报警状态，0-正常、1-预警、2-报警。

    @Builder.Default
    protected int runstate = 1; // 模块运行状态，1-正常、2-维修、3-故障。

    protected double latitude; // 纬度。
    protected double longitude; // 经度。
    protected double altitude; // 高度。

    @Override
    public AbstractMod clone() throws CloneNotSupportedException {
        return (AbstractMod)super.clone();
    }
}
