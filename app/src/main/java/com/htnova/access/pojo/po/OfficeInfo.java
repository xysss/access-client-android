package com.htnova.access.pojo.po;

import com.htnova.access.commons.pojo.AbstractTreeNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 机构信息实体。 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OfficeInfo extends AbstractTreeNode {
    private String parentIds;
    private String code;
    private String type;
    private int grade;
    private String address;
    private String stationMap;
    private String centerCoordinate;

    public OfficeInfo() {
        super();
        this.nodeType = 1;
    }
}
