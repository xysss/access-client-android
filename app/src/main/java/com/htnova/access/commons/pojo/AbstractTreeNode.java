package com.htnova.access.commons.pojo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/** 树节点抽象。 */
@Data
public class AbstractTreeNode implements Cloneable, java.io.Serializable {
    protected String id; // 流水号。
    protected String parentId; // 父流水号。
    protected String name; // 名称。
    protected int leafFlag; // 叶节点标识：1-叶节点，0-非叶节点。
    protected int nodeType; // 节点类别：1-机构，2-设备。
    protected double latitude; // 纬度。
    protected double longitude; // 经度。
    protected double altitude; // 高度。
    protected String pointCoordinate; // 自定义坐标：空格分开的xy坐标，如-126.88110888 78.82585074。
    protected List<AbstractTreeNode> children; // 子节点。

    public void add(AbstractTreeNode node) {
        // 当前树型结构的实现，根节点都为0。
        if ("0".equals(node.parentId)) {
            addNode();
            this.children.add(node);
        } else if (node.parentId.equals(this.id)) {
            addNode();
            this.children.add(node);
        } else {
            if (this.children != null) {
                for (int i = 0; i < children.size(); i++) {
                    AbstractTreeNode tempNode = children.get(i);
                    tempNode.add(node);
                }
            }
        }
    }

    private void addNode() {
        if (this.children == null) {
            this.setChildren(new ArrayList<>());
        }
    }

    public Object deepClone() throws IOException, ClassNotFoundException {
        // 通过序列化、反序列化的方式，完成对象的深克隆，普通的方式只能完成浅克隆。
        // 序列化。
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);

        // 反序列化。
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }
}
