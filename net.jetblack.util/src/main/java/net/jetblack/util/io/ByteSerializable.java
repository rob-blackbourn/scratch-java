package net.jetblack.util.io;

public interface ByteSerializable {
    Object deserialize(byte[] bytes) throws Exception;
    byte[] serialize(Object data) throws Exception;
}
