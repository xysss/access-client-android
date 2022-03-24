package com.htnova.access.datahandle.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 警报信息详情。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDataDto {
    private String id;
    private String infoId;
    private String data;
}
