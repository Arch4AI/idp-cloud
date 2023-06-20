package com.dataease.cloud.utils.wx;


import java.util.ArrayList;

/**
 * Byte Group
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/5/17 3:57 PM
 **/
class ByteGroup {
    ArrayList<Byte> byteContainer = new ArrayList<Byte>();

    public byte[] toBytes() {
        byte[] bytes = new byte[byteContainer.size()];
        for (int i = 0; i < byteContainer.size(); i++) {
            bytes[i] = byteContainer.get(i);
        }
        return bytes;
    }

    public ByteGroup addBytes(byte[] bytes) {
        for (byte b : bytes) {
            byteContainer.add(b);
        }
        return this;
    }

    public int size() {
        return byteContainer.size();
    }
}
