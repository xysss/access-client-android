package com.htnova.access.pojo.param;

import lombok.Data;

@Data
public class PageParam {
    private Integer offset = 1;
    private Integer limit = 10;
}
