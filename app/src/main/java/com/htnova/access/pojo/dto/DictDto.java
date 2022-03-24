package com.htnova.access.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class DictDto {
    private String label;
    private String value;

    @JsonIgnore
    private String type;
}
