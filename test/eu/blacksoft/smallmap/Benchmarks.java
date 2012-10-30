package eu.blacksoft.smallmap;

import eu.blacksoft.smallmap.TimingUtils.Timer;
import gnu.trove.map.hash.THashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 *
 */
public class Benchmarks {
	private static final int NO_OF_OBJ_FOR_MEM_TEST = 70000;
	private static final int NO_OF_TESTS = 5000000;

	public <K, V> void runTests(Map<K, V> mapUnderTest, @SuppressWarnings("unchecked") K... keys) {
		for (int i = 0; i < NO_OF_TESTS; ++i) {
			for (K key : keys) {
				mapUnderTest.get(key);
			}
		}
	}

	@Test
	public void benchmarkGetForOneElement() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("abc", 1234);

		// when
		runAllTests("1", prepareTestInput(of), "abc");

		// then
	}

	@Test
	public void benchmarkGetForTwoElements() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("abc", 123, "def", 456);

		// when
		runAllTests("2", prepareTestInput(of), "def", "abc", "def", "def", "abc", "def", "abc",
				"abc");

		// then
	}

	@Test
	public void benchmarkGetForThreeElements() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("abc", 123, "def", 456, "xyz", 1455);
		// when
		runAllTests("3", prepareTestInput(of), "def", "xyz", "abc", "xyz", "def", "def", "xyz",
				"abc", "def", "xyz", "abc", "abc");

		// then
	}

	@Test
	public void benchmarkGetForFourElements() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("zzz", 12344, "abc", 123, "def", 456,
				"xyz", 1455);

		// when
		runAllTests("4", prepareTestInput(of), "zzz", "def", "xyz", "abc", "zzz", "xyz", "def",
				"def", "xyz", "zzz", "abc", "def", "xyz", "zzz", "abc", "zzz", "abc");

		// then
	}

	@Test
	public void benchmarkGetForFiveElements() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("123", 6661, "zzz", 12344, "abc", 123,
				"def", 456, "xyz", 1455);

		// when
		runAllTests("5", prepareTestInput(of), "zzz", "def", "123", "xyz", "abc", "zzz", "123",
				"123", "xyz", "def", "def", "xyz", "zzz", "123", "abc", "def", "123", "xyz", "zzz",
				"abc", "123", "zzz", "abc");

		// then
	}

	private ImmutableMap<String, Map<String, Integer>> prepareTestInput(
			ImmutableMap<String, Integer> of) {
		Map<String, Integer> hashMap = Maps.newHashMap();
		Map<String, Integer> smallMap = SmallMap.newSmallMap();

		hashMap.putAll(of);
		smallMap.putAll(of);
		return ImmutableMap.of("HashMap", hashMap, "Immutable", of, "SmallMap", smallMap);
	}

	private long getMemory() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}

	@Test
	public void shouldHaveLowerFootprint() {
		// given
		ImmutableMap<String, Integer> of = ImmutableMap.of("123", 6661, "zzz", 12344, "abc", 123,
				"def", 456, "xyz", 1455);
		long memory = getMemory();

		@SuppressWarnings("unchecked")
		SmallMap<String, Integer>[] maps = new SmallMap[NO_OF_OBJ_FOR_MEM_TEST];
		for (int i = 0; i < NO_OF_OBJ_FOR_MEM_TEST; ++i) {
			maps[i] = SmallMap.newSmallMap();
			maps[i].putAll(of);
		}
		printMemoryUsage("SmallMap", memory);

		// create hashmaps
		memory = getMemory();
		@SuppressWarnings("unchecked")
		HashMap<String, Integer>[] maps2 = new HashMap[NO_OF_OBJ_FOR_MEM_TEST];
		for (int i = 0; i < NO_OF_OBJ_FOR_MEM_TEST; ++i) {
			maps2[i] = new HashMap<String, Integer>(1);
			maps2[i].putAll(of);
		}
		printMemoryUsage("HashMap", memory);

		// trove maps
		memory = getMemory();
		@SuppressWarnings("unchecked")
		THashMap<String, Integer>[] maps3 = new THashMap[NO_OF_OBJ_FOR_MEM_TEST];
		for (int i = 0; i < NO_OF_OBJ_FOR_MEM_TEST; ++i) {
			maps3[i] = new THashMap<String, Integer>(1);
			maps3[i].putAll(of);
		}
		printMemoryUsage("Trove", memory);

		// Immutable maps
		memory = getMemory();
		@SuppressWarnings("unchecked")
		ImmutableMap<String, Integer>[] maps4 = new ImmutableMap[NO_OF_OBJ_FOR_MEM_TEST];
		for (int i = 0; i < NO_OF_OBJ_FOR_MEM_TEST; ++i) {
			maps4[i] = ImmutableMap.<String, Integer> builder().putAll(of).build();
		}
		printMemoryUsage("Immutable", memory);

	}

	/**
	 * @param name
	 * @param startMemory
	 */
	private void printMemoryUsage(String name, long startMemory) {
		System.err.println("[" + name + "] Used memory: "
				+ Math.floor((getMemory() - startMemory) / 10000));
	}

	private void runAllTests(String fora, ImmutableMap<String, Map<String, Integer>> tests,
			String... keys) {
		for (Entry<String, Map<String, Integer>> entry : tests.entrySet()) {
			Timer startTimer = TimingUtils.startTimer(TestSmallMapTest.class, entry.getKey()
					+ " for " + fora);
			runTests(entry.getValue(), keys);
			startTimer.stopAndLog();
		}
		TimingUtils.getInstance(TestSmallMapTest.class).log("----");
	}
}
