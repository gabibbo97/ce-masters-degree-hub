package logger;

public class MyLogger {
	private static MyLogger instance;

	public static MyLogger getInstance() {
		if (instance == null) {
			instance = new MyLogger();
		}
		return instance;
	}

	private final long initialTimeMillis;

	public MyLogger() {
		initialTimeMillis = System.currentTimeMillis();
	}

	public void log(String message) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		final StringBuilder logline = new StringBuilder();

		final long elapsedMillis = System.currentTimeMillis() - initialTimeMillis;
		logline.append(String.format("[ %s ] ", new Day().formatMillis(elapsedMillis)));
		if (stackTrace.length > 2) {
			final StackTraceElement caller = stackTrace[2];
			logline.append(String.format("[ %s:%s:%d ] ", caller.getClassName(), caller.getMethodName(),
					caller.getLineNumber()));
		}
		logline.append(message);
		System.out.println(logline.toString());
	}
}
