package eu.blacksoft.smallmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

/**
 *
 */
public class TimingUtils {

	private final static Map<TimeUnit, String> DisplayName = ImmutableMap
			.<TimeUnit, String> builder().put(TimeUnit.DAYS, "d").put(TimeUnit.HOURS, "h")
			.put(TimeUnit.MICROSECONDS, "us").put(TimeUnit.MILLISECONDS, "ms")
			.put(TimeUnit.MINUTES, "min").put(TimeUnit.NANOSECONDS, "ns")
			.put(TimeUnit.SECONDS, "s").build();

	private long lastTime;

	private final String clazz;

	public TimingUtils(Class<?> clazz) {
		this.clazz = clazz.getSimpleName();
	}

	private void displayLog(String string) {
		System.out.println("[" + clazz + "]" + string);
	}

	public void log(String string) {
		long deltaTime = updateLastTimeAndGetDelta();
		displayLog("(" + getTime() + ") [+" + deltaTime + "ms]" + string);
	}

	private long updateLastTimeAndGetDelta() {
		long oldLastTime = lastTime;
		lastTime = System.currentTimeMillis();
		return oldLastTime > 0 ? lastTime - oldLastTime : 0;
	}

	private String getTime() {
		return new SimpleDateFormat("k:m:s.S").format(new Date());
	}

	public Timer startTimer(String description) {
		return new Timer(description).start();
	}

	public static TimingUtils getInstance(Class<?> clazz) {
		return new TimingUtils(clazz);
	}

	public static Timer startTimer(Class<?> class1, String string) {
		return getInstance(class1).startTimer(string);
	}

	public class Timer {

		private final String description;
		private final Stopwatch stopwatch;

		public Timer(String description) {
			this.description = description;
			this.stopwatch = new Stopwatch();
		}

		public Timer start() {
			stopwatch.start();
			return this;
		}

		public void stopAndLog() {
			stopwatch.stop();
			printOutput(TimeUnit.MILLISECONDS);
		}

		private void printOutput(TimeUnit desiredUnit) {
			long elapsedTime = stopwatch.elapsedTime(desiredUnit);
			displayLog("-- [" + description + "] Elapsed time: " + elapsedTime
					+ DisplayName.get(desiredUnit));
		}

	}
}
