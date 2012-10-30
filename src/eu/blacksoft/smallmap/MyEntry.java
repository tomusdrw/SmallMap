package eu.blacksoft.smallmap;

import java.util.Map;

class MyEntry<K, V> implements Map.Entry<K, V> {

	private final K key;
	private final V value;

	public MyEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		throw new IllegalArgumentException("This is immutable entry!");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		Map.Entry other = (Map.Entry) obj;
		/*
		 * NOTE: null keys and values are not allowed.
		 */
		if (!key.equals(other.getKey())) {
			return false;
		}
		if (!value.equals(other.getValue())) {
			return false;
		}
		return true;
	}

}