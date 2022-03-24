package com.htnova.access.pojo.param;

import java.util.List;

import com.htnova.access.pojo.dto.SensorData;

import lombok.Data;

@Data
public class Fcbr100mRequestData {
    private byte sensorType;
    private List<SensorData> sensors;
}