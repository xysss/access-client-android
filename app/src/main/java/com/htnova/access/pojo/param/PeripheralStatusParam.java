package com.htnova.access.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeripheralStatusParam {
    private int state; // 当前滤毒罐的状态：0-关闭，1-开启。
    private int operator; // 当前的操作对象：0-手动操作，1-报警操作，2-远程手动操作。
    private long beginTime; // 滤毒罐的开机时刻
}
