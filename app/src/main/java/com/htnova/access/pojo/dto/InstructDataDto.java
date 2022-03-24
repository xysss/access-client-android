package com.htnova.access.pojo.dto;

import com.htnova.access.commons.pojo.AbstractDevice;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InstructDataDto extends AbstractDevice {
    // 操作结果：成功失败的标志。
    private int result;

    public InstructDataDto() {
        super();
        this.dataType = DATA_TYPE_INSTRUCT;
    }
}
