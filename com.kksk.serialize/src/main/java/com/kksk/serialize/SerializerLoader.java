package com.kksk.serialize;

import com.kksk.serialize.spi.SerializerProvider;
import com.kksk.spi.ServiceProvider;
import com.kksk.spi.ServiceProviderRegistry;
import com.kksk.spi.ServiceRegistry;

public final class SerializerLoader {
	private SerializerLoader() {
	}

	private static final ServiceRegistry<String, Serializer> SERVICE_REGISTRY = new ServiceRegistry<String, Serializer>() {
		@Override
		protected ServiceProviderRegistry<String, Serializer> getServiceProviderRegistry() {
			return new ServiceProviderRegistry<String, Serializer>() {
				@Override
				protected Class<? extends ServiceProvider<String, Serializer>> getProviderClass() {
					return SerializerProvider.class;
				}
			};
		}
	};

	public static void setDefaultClassLoader(ClassLoader classLoader) {
		SERVICE_REGISTRY.setDefaultClassLoader(classLoader);
	}

	public static Serializer getSerializer(String key) {
		return SERVICE_REGISTRY.getService(key);
	}
}
