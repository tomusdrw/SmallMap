package eu.blacksoft.smallmap;

import java.util.Map;

public interface MapCreator {
	<K, V> Map<K, V> create(int initialSize);
}