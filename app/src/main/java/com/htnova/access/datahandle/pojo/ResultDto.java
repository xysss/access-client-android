package com.htnova.access.datahandle.pojo;

import com.htnova.access.commons.message.MessageEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 后端数据对象在前端的封装，不同的类通过该结构进行封装和转换。 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto<T> {
    private int code; // 返回的状态格式，根据状态值判断返回成功或失败。

    @Builder.Default
    private String message = ""; // 返回的提示消息，用于描述返回的状态。

    private T data;

    public ResultDto(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDto(MessageEnum message) {
        this.code = message.getCode();
        this.message = message.getMessage();
    }
}
