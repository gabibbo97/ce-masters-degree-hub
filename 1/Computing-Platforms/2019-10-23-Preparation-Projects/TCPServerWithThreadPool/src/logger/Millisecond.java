package logger;

public class Millisecond extends GreedyTimeIntervalUnit {

	@Override
	protected GreedyTimeIntervalUnit getDivisorUnit() {
		return null;
	}

	@Override
	protected long getMillisConversion() {
		return 1;
	}

	@Override
	protected String getUnitName() {
		return "ms";
	}

}
