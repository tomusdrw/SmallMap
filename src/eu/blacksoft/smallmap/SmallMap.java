package eu.blacksoft.smallmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Does not accept null keys or values!.
 */
public class SmallMap<K, V> implements Map<K, V> {
	private static final int DEFAULT_SIZE = 5;
	private static final MapCreator DEFAULT_CREATOR = new MapCreator() {
		@Override
		public <K, V> Map<K, V> create(int initialSize) {
			return new HashMap<K, V>(initialSize);
		}
	};

	private boolean isBig = false;

	private int maxIdx = 0;
	private final K[] keys;
	private final V[] values;

	private Map<K, V> backingMap = null;
	private final MapCreator mapCreator;

	public SmallMap() {
		this(DEFAULT_SIZE, DEFAULT_CREATOR);
	}

	public SmallMap(int size) {
		this(size, DEFAULT_CREATOR);
	}

	public SmallMap(MapCreator creator) {
		this(DEFAULT_SIZE, creator);
	}

	@SuppressWarnings("unchecked")
	public SmallMap(int size, MapCreator mapCreator) {
		this.mapCreator = mapCreator;
		this.keys = (K[]) new Object[size];
		this.values = (V[]) new Object[size];
	}

	private void convertToBackingMap() {
		backingMap = mapCreator.create(maxIdx);
		for (int i = 0; i < maxIdx; ++i) {
			backingMap.put(keys[i], values[i]);
		}
		clearArrays();
		isBig = true;
	}

	@Override
	public int size() {
		if (isBig) {
			return backingMap.size();
		}
		return maxIdx;
	}

	@Override
	public boolean isEmpty() {
		if (isBig) {
			return backingMap.isEmpty();
		}

		return maxIdx == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		checkNullArgument(key);
		if (isBig) {
			return backingMap.containsKey(key);
		}
		return findIndex(keys, maxIdx, key) != -1;
	}

	@Override
	public boolean containsValue(Object value) {
		checkNullArgument(value);
		if (isBig) {
			return backingMap.containsValue(value);
		}
		return findIndex(values, maxIdx, value) != -1;
	}

	@Override
	public V get(Object key) {
		checkNullArgument(key);
		if (isBig) {
			return backingMap.get(key);
		}
		int idx = findIndex(keys, maxIdx, key);
		if (idx != -1) {
			return values[idx];
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		checkNullArgument(key);
		checkNullArgument(value);
		if (isBig) {
			return backingMap.put(key, value);
		}
		int idx = findIndex(keys, maxIdx, key);
		if (idx == -1) {
			// check if we don't need to convert
			if (maxIdx == keys.length) {
				convertToBackingMap();
				return backingMap.put(key, value);
			}
			// get new index
			idx = maxIdx++;
			keys[idx] = key;
		}
		V previous = values[idx];
		values[idx] = value;

		return previous;
	}

	@Override
	public V remove(Object key) {
		checkNullArgument(key);
		if (isBig) {
			return backingMap.remove(key);
		}
		int idx = findIndex(keys, maxIdx, key);
		if (idx == -1) {
			return null;
		}

		V value = values[idx];
		// move last element in place of removed one
		int last = --maxIdx;

		keys[idx] = keys[last];
		values[idx] = values[last];
		// clear last elements
		keys[last] = null;
		values[last] = null;

		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m.size() > keys.length - maxIdx) {
			convertToBackingMap();
			backingMap.putAll(m);
			return;
		}
		// just put elements because they will fit in arrays
		for (java.util.Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		if (isBig) {
			backingMap.clear();
			backingMap = null;
			isBig = false;
		}
		// free arrays
		clearArrays();
	}

	private void clearArrays() {
		for (int i = 0; i < maxIdx; ++i) {
			keys[i] = null;
			values[i] = null;
		}
		maxIdx = 0;
	}

	@Override
	public Set<K> keySet() {
		if (isBig) {
			return backingMap.keySet();
		}
		if (maxIdx > 0) {
			Set<K> set = new LinkedHashSet<K>(maxIdx);
			addAll(set, keys);
			return Collections.unmodifiableSet(set);
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<V> values() {
		if (isBig) {
			return backingMap.values();
		}
		if (maxIdx > 0) {
			List<V> list = new ArrayList<V>(maxIdx);
			addAll(list, values);
			return Collections.unmodifiableList(list);
		}
		return Collections.emptyList();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if (isBig) {
			return backingMap.entrySet();
		}
		Set<Map.Entry<K, V>> set = new LinkedHashSet<Map.Entry<K, V>>(maxIdx);
		for (int i = 0; i < maxIdx; ++i) {
			set.add(new MyEntry<K, V>(keys[i], values[i]));
		}
		return Collections.unmodifiableSet(set);
	}

	@Override
	public boolean equals(Object obj) {
		if (isBig) {
			return backingMap.equals(obj);
		}

		if (!(obj instanceof Map)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Map<K, V> m = (Map<K, V>) obj;
		if (m.size() != size()) {
			return false;
		}

		// iterate over arrays
		for (int i = 0; i < maxIdx; ++i) {
			if (!m.containsKey(keys[i])) {
				return false;
			}
			V v = m.get(keys[i]);
			// null values not allowed
			if (v == null) {
				return false;
			}
			if (!v.equals(values[i])) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		if (isBig) {
			return backingMap.hashCode();
		}
		int hashCode = 0;
		for (int i = 0; i < maxIdx; ++i) {
			hashCode += keys[i].hashCode() ^ values[i].hashCode();
		}
		return hashCode;
	}

	@Override
	public String toString() {
		if (isBig) {
			return "[Big]" + backingMap.toString();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[Small]{");
		if (maxIdx > 0) {
			appendElement(sb, 0);
			for (int i = 1; i < maxIdx; ++i) {
				sb.append(", ");
				appendElement(sb, i);

			}
		}
		sb.append('}');
		return sb.toString();
	}

	private void appendElement(StringBuilder sb, int idx) {
		sb.append(keys[idx] == this ? "(this Map)" : keys[idx]);
		sb.append("=");
		sb.append(values[idx] == this ? "(this Map)" : values[idx]);
	}

	private static void checkNullArgument(Object key) {
		if (key == null) {
			throw new NullPointerException("This map does not accept null keys or values.");
		}
	}

	private static <K> void addAll(Collection<K> collection, K[] array) {
		for (K k : array) {
			collection.add(k);
		}
	}

	private static int findIndex(Object[] array, int maxIdx, Object key) {
		for (int i = 0; i < maxIdx; i++) {
			if (array[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}

	public static <K, V> SmallMap<K, V> newSmallMap() {
		return new SmallMap<K, V>();
	}

	public static <K, V> SmallMap<K, V> newSmallMap(int i) {
		return new SmallMap<K, V>(i);
	}
}
