package logger;

public abstract class GreedyTimeIntervalUnit {
	protected String formatMillis(long millis) {
		final StringBuilder timeString = new StringBuilder();

		final long myAmount = millis / getMillisConversion();
		final long leftOverMillis = millis % getMillisConversion();

		if (getDivisorUnit() == null) {
			timeString.append(String.format("%d %s", myAmount, getUnitName()));
		} else if (myAmount > 0) {
			timeString.append(
					String.format("%d %s %s", myAmount, getUnitName(), getDivisorUnit().formatMillis(leftOverMillis)));
		} else {
			timeString.append(getDivisorUnit().formatMillis(leftOverMillis));
		}

		return timeString.toString().trim();
	}

	protected abstract GreedyTimeIntervalUnit getDivisorUnit();

	protected abstract long getMillisConversion();

	protected abstract String getUnitName();
}
