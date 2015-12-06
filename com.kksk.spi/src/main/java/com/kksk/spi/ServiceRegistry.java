package com.kksk.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public abstract class ServiceRegistry<Key, ProvidableService> {
	private WeakHashMap<ClassLoader, Map<Key, ProvidableService>> allServices;
	private final ServiceProviderRegistry<Key, ProvidableService> SERVICE_PROVIDERS = getServiceProviderRegistry();

	protected ServiceRegistry() {
		allServices = new WeakHashMap<>();
	}

	public ClassLoader getDefaultClassLoader() {
		return SERVICE_PROVIDERS.getDefaultClassLoader();
	}

	public void setDefaultClassLoader(ClassLoader classLoader) {
		SERVICE_PROVIDERS.setDefaultClassLoader(classLoader);
	}

	public Map<Key, ProvidableService> getServiceMap() {
		return getServiceMap(null);
	}

	public ProvidableService getService(Key key) {
		return getServiceMap().get(key);
	}

	protected abstract ServiceProviderRegistry<Key, ProvidableService> getServiceProviderRegistry();

	public Map<Key, ProvidableService> getServiceMap(ClassLoader classLoader) {
		final ClassLoader serviceClassLoader = classLoader == null ? getDefaultClassLoader() : classLoader;
		Map<Key, ProvidableService> serviceMap = allServices.get(serviceClassLoader);
		if (serviceMap == null) {
			synchronized (allServices) {
				serviceMap = new LinkedHashMap<Key, ProvidableService>();
				Map<String, ServiceProvider<Key, ProvidableService>> providers = SERVICE_PROVIDERS.getProviders(serviceClassLoader);
				for (ServiceProvider<Key, ProvidableService> provider : providers.values()) {
					for (Entry<Key, ProvidableService> entry : provider.getServiceMap().entrySet()) {
						serviceMap.put(entry.getKey(), entry.getValue());
					}
				}
				allServices.put(serviceClassLoader, serviceMap);
			}
		}
		return serviceMap;
	}
}
