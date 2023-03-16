package com.dataease.cloud.constant;

/**
 * 性别枚举
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 2:46 PM
**/
public enum GenderEnum {
    MAN(1),
    WOMAN(2);

    private int key;

    GenderEnum(int key){
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
