package com.kksk.serialize;

public interface Serializer {
	byte[] serialize(Object obejct);

	Object deserialize(byte[] bytes);
}
