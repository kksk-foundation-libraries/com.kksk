package com.kksk.serialize.spi;

import com.kksk.serialize.Serializer;
import com.kksk.spi.ServiceProvider;

public interface SerializerProvider extends ServiceProvider<String, Serializer> {
}
