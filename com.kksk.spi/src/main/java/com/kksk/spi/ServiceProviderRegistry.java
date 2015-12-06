package com.kksk.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

public abstract class ServiceProviderRegistry<Key, ProvidableService> {
	protected WeakHashMap<ClassLoader, LinkedHashMap<String, ServiceProvider<Key, ProvidableService>>> serviceProviders;

	protected volatile ClassLoader classLoader;

	public ServiceProviderRegistry() {
		this.serviceProviders = new WeakHashMap<>();
		this.classLoader = null;
	}

	public ClassLoader getDefaultClassLoader() {
		ClassLoader loader = classLoader;
		return loader == null ? Thread.currentThread().getContextClassLoader() : loader;
	}

	public void setDefaultClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected abstract Class<? extends ServiceProvider<Key, ProvidableService>> getProviderClass();

	public Map<String, ServiceProvider<Key, ProvidableService>> getProviders() {
		return getProviders(getDefaultClassLoader());
	}

	public Map<String, ServiceProvider<Key, ProvidableService>> getProviders(ClassLoader classLoader) {
		final ClassLoader serviceClassLoader = classLoader == null ? getDefaultClassLoader() : classLoader;
		LinkedHashMap<String, ServiceProvider<Key, ProvidableService>> providers = serviceProviders.get(serviceClassLoader);
		if (providers == null) {
			synchronized (serviceProviders) {
				providers = AccessController.doPrivileged(new PrivilegedAction<LinkedHashMap<String, ServiceProvider<Key, ProvidableService>>>() {
					@Override
					public LinkedHashMap<String, ServiceProvider<Key, ProvidableService>> run() {
						LinkedHashMap<String, ServiceProvider<Key, ProvidableService>> result = new LinkedHashMap<String, ServiceProvider<Key, ProvidableService>>();

						ServiceLoader<? extends ServiceProvider<Key, ProvidableService>> serviceLoader = ServiceLoader.load(getProviderClass(), serviceClassLoader);
						for (ServiceProvider<Key, ProvidableService> provider : serviceLoader) {
							result.put(provider.getClass().getName(), provider);
						}
						return result;
					}
				});
				serviceProviders.put(serviceClassLoader, providers);
			}
		}
		return providers;
	}
}
