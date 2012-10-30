package eu.blacksoft.smallmap;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fest.assertions.Fail;
import org.junit.Test;
import org.mockito.Mockito;

public class TestSmallMapTest {

	@Test
	public void shouldAddAndGetElement() {
		// given
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap();

		// when
		newSmallMap.put("abc", 155);
		newSmallMap.put("def", 456);

		// then
		assertThat(newSmallMap.get("abc")).isEqualTo(155);
		assertThat(newSmallMap.get("def")).isEqualTo(456);
		assertThat(newSmallMap.get("xyz")).isNull();
	}

	@Test
	public void shouldRemoveElement() {
		// given
		SmallMap<String, Integer> newSmallMap = smallTestMap();
		int size = newSmallMap.size();

		// when
		newSmallMap.remove("abc");

		// then
		assertThat(newSmallMap.get("def")).isEqualTo(456);
		assertThat(newSmallMap.get("xyz")).isEqualTo(123);
		assertThat(newSmallMap.get("abc")).isNull();
		assertThat(newSmallMap.size()).isEqualTo(size - 1);
	}

	@Test
	public void shouldReturnProperSize() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();
		assertThat(smallTestMap.size()).isEqualTo(3);

		// when
		smallTestMap.put("123", 1234);

		// then
		assertThat(smallTestMap.size()).isEqualTo(4);
	}

	@Test
	public void sameMapsShouldHaveSameHashcodes() {
		// given
		SmallMap<String, Integer> map1 = smallTestMap();
		SmallMap<String, Integer> map2 = smallTestMap();

		// when

		// then
		assertThat(map1.hashCode()).isEqualTo(map2.hashCode());
	}

	@Test
	public void sameMapsShouldBeEqual() {
		// given
		SmallMap<String, Integer> map1 = smallTestMap();
		SmallMap<String, Integer> map2 = smallTestMap();

		// when

		// then
		assertThat(map1).isEqualTo(map2);
		assertThat(map2).isEqualTo(map1);
		assertThat(map1).isEqualTo(map1);
	}

	@Test
	public void sameMapsButDifferentTypesShouldBeEqual() {
		// given
		SmallMap<String, Integer> map1 = SmallMap.newSmallMap();
		map1.put("abc", 123);
		map1.put("xyz", 456);
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("abc", 123);
		map2.put("xyz", 456);

		// when

		// then
		assertThat(map1).isEqualTo(map2);
		assertThat(map2).isEqualTo(map1);
		assertThat(map1).isEqualTo(map1);
	}

	@Test
	public void differentMapsShouldNotBeEqual() {
		// given
		SmallMap<String, Integer> map1 = SmallMap.newSmallMap();
		map1.put("abc", 123);
		map1.put("xyz", 456);
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("abc", 123);
		map2.put("xyz", 123);

		// when

		// then
		assertThat(map1).isNotEqualTo(map2);
		assertThat(map2).isNotEqualTo(map1);
	}

	@Test
	public void shouldNotEqualDifferentObject() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();

		// when

		// then
		assertThat(smallTestMap.equals(15)).isFalse();
	}

	@Test
	public void shouldReturnKeySet() {
		// given
		SmallMap<String, Integer> map1 = smallTestMap();

		// when
		Set<String> keySet = map1.keySet();

		// then
		assertThat(keySet).containsOnly("xyz", "abc", "def");
	}

	@Test
	public void shouldReturnPreviousValueOnPut() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();
		Integer before = smallTestMap.put("xxxx", 1234);

		// when
		Integer after = smallTestMap.put("xxxx", 166);

		// then
		assertThat(before).isEqualTo(null);
		assertThat(after).isEqualTo(1234);
	}

	@Test
	public void shouldReturnValuesCollection() {
		// given
		SmallMap<String, Integer> map1 = smallTestMap();

		// when
		Collection<Integer> values = map1.values();

		// then
		assertThat(values).containsOnly(123, 155, 456);
	}

	@Test
	public void shouldReturnEntrySet() {
		// given
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap(3);
		newSmallMap.put("abc", 123);
		newSmallMap.put("def", 456);

		// when
		Set<Entry<String, Integer>> entrySet = newSmallMap.entrySet();

		// then
		assertThat(entrySet).containsOnly(new MyEntry<String, Integer>("abc", 123),
				new MyEntry<String, Integer>("def", 456));
	}

	@Test
	public void shouldContainKeys() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();
		smallTestMap.put("abc", 123);

		// when
		boolean containsKey = smallTestMap.containsKey("abc");
		boolean doesNotContainKey = smallTestMap.containsKey("111");

		// then
		assertThat(containsKey).isTrue();
		assertThat(doesNotContainKey).isFalse();
	}

	@Test
	public void emptyMapShouldBeEmpty() {
		// given
		SmallMap<Object, Object> newSmallMap = SmallMap.newSmallMap();

		// when
		boolean empty = newSmallMap.isEmpty();
		int size = newSmallMap.size();

		// then
		assertThat(empty).isTrue();
		assertThat(size).isZero();
	}

	@Test
	public void shouldReturnNullIfRemovingNotExistingElement() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();

		// when
		Integer remove = smallTestMap.remove("1111");

		// then
		assertThat(remove).isNull();
	}

	@Test
	public void shouldClearMap() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();
		assertThat(smallTestMap.isEmpty()).isFalse();

		// when
		smallTestMap.clear();

		// then
		assertThat(smallTestMap.isEmpty()).isTrue();
		assertThat(smallTestMap.size()).isZero();
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAcceptNullKeys() {
		// given
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap();

		// when
		newSmallMap.put(null, 123);

		// then
		Fail.fail("NullPointerException expected");
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAcceptNullValues() {
		// given
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap();

		// when
		newSmallMap.put("abc", null);

		// then
		Fail.fail("NullPointerException expected");
	}

	@Test
	public void shouldReturnTrueOnContainsValue() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();

		// when
		boolean containsValue = smallTestMap.containsValue(155);
		boolean containsValue2 = smallTestMap.containsValue(155555);

		// then
		assertThat(containsValue).isTrue();
		assertThat(containsValue2).isFalse();
	}

	@Test
	public void shouldPutAllElements() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap();

		// when
		newSmallMap.putAll(smallTestMap);

		// then
		assertThat(newSmallMap).isEqualTo(smallTestMap);
		assertThat(smallTestMap).isEqualTo(newSmallMap);
	}

	@Test
	public void shouldReturnToStringRepresentation() {
		// given
		SmallMap<String, Integer> smallTestMap = smallTestMap();

		// when
		String string = smallTestMap.toString();

		// then
		assertThat(string).isEqualTo("[Small]{xyz=123, abc=155, def=456}");
	}

	/*------------------- Big map -------------------- */

	@Test
	public void shouldWorkOnBigMap() {
		// given
		SmallMap<String, Integer> newSmallMap = bigTestMap();

		// when

		// then
		assertThat(newSmallMap.get("def")).isEqualTo(456);
		assertThat(newSmallMap.get("xyz")).isEqualTo(123);
		assertThat(newSmallMap.get("abc")).isEqualTo(155);
	}

	@Test
	public void shouldConvertToBigMap() {
		// given
		Map<String, Integer> mapMock = createMapMock();
		SmallMap<String, Integer> smallMap = createSmallMap(mapMock);

		// when
		smallMap.put("abc", 123);
		smallMap.put("xyz", 456);

		// then
		Mockito.verify(mapMock).put("abc", 123);
		Mockito.verify(mapMock).put("xyz", 456);
		Mockito.verifyNoMoreInteractions(mapMock);
	}

	@Test
	public void shouldDelegateToCreatedMapWhenBig() {
		// given
		SmallMap<String, Integer> map2 = smallTestMap();
		Map<String, Integer> mapMock = createMapMock();
		SmallMap<String, Integer> smallMap = createSmallMap(mapMock);
		fillAndVerify(mapMock, smallMap);

		// when
		smallMap.put("abc", 123);
		Mockito.verify(mapMock).put("abc", 123);

		// when
		smallMap.containsKey("abc");
		Mockito.verify(mapMock).containsKey("abc");

		// when
		smallMap.containsValue(123);
		Mockito.verify(mapMock).containsValue(123);

		// when
		smallMap.entrySet();
		Mockito.verify(mapMock).entrySet();

		// when
		smallMap.values();
		Mockito.verify(mapMock).values();

		// when
		smallMap.keySet();
		Mockito.verify(mapMock).keySet();

		// when
		smallMap.get("abc");
		Mockito.verify(mapMock).get("abc");

		// when
		smallMap.isEmpty();
		Mockito.verify(mapMock).isEmpty();

		// when
		smallMap.size();
		Mockito.verify(mapMock).size();

		// when
		smallMap.putAll(map2);
		Mockito.verify(mapMock).putAll(map2);

		// when
		smallMap.remove("abc");
		Mockito.verify(mapMock).remove("abc");

		// then
		Mockito.verifyNoMoreInteractions(mapMock);
	}

	@Test
	public void shouldDelegateClearAndResetState() {
		// given
		Map<String, Integer> mapMock = createMapMock();
		SmallMap<String, Integer> smallMap = createSmallMap(mapMock);
		fillAndVerify(mapMock, smallMap);

		// when
		smallMap.clear();
		smallMap.put("abc", 123);

		// then
		Mockito.verify(mapMock).clear();
		// we want no more interaction although there was put
		Mockito.verifyNoMoreInteractions(mapMock);
	}

	private void fillAndVerify(Map<String, Integer> mapMock, SmallMap<String, Integer> smallMap) {
		smallMap.put("123", 123);
		smallMap.put("456", 456);
		Mockito.verify(mapMock).put("123", 123);
		Mockito.verify(mapMock).put("456", 456);
	}

	private SmallMap<String, Integer> createSmallMap(final Map<String, Integer> mapMock) {
		SmallMap<String, Integer> smallMap = new SmallMap<String, Integer>(1, new MapCreator() {
			@SuppressWarnings("unchecked")
			@Override
			public <K, V> Map<K, V> create(int initialSize) {
				return (Map<K, V>) mapMock;
			}
		});
		return smallMap;
	}

	private Map<String, Integer> createMapMock() {
		@SuppressWarnings("unchecked")
		final Map<String, Integer> mapMock = Mockito.mock(Map.class);
		return mapMock;
	}

	private SmallMap<String, Integer> smallTestMap() {
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap(3);
		newSmallMap.put("xyz", 123);
		newSmallMap.put("abc", 155);
		newSmallMap.put("def", 456);
		return newSmallMap;
	}

	private SmallMap<String, Integer> bigTestMap() {
		SmallMap<String, Integer> newSmallMap = SmallMap.newSmallMap(2);
		newSmallMap.put("xyz", 123);
		newSmallMap.put("abc", 155);
		newSmallMap.put("def", 456);
		return newSmallMap;
	}

}
