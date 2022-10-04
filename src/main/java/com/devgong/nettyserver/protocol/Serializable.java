package com.devgong.nettyserver.protocol;

public interface Serializable<T> {
    T deserialize(byte[] byteArray);

    byte[] serialize();
    byte[] reportSerialize();

}
