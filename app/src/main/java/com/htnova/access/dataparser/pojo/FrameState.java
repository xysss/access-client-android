package com.htnova.access.dataparser.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FrameState {
    private List<byte[]> currFrames;
    private byte[] remainData;

    public void addCurrFrame(byte[] tempCurrFrame) {
        if (currFrames == null) {
            currFrames = new ArrayList<>();
        }
        currFrames.add(tempCurrFrame);
    }

    public boolean isEmpty() {
        return (currFrames == null && remainData == null);
    }
}
