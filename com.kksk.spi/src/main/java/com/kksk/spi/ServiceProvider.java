package com.kksk.spi;

import java.util.Map;

public interface ServiceProvider<Key, ProvidableService> {
	Map<Key, ProvidableService> getServiceMap();
	ProvidableService getService(Key key);
}
