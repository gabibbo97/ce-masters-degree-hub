package logger;

public class Minute extends GreedyTimeIntervalUnit {

	@Override
	protected GreedyTimeIntervalUnit getDivisorUnit() {
		return new Second();
	}

	@Override
	protected long getMillisConversion() {
		return getDivisorUnit().getMillisConversion() * 60;
	}

	@Override
	protected String getUnitName() {
		return "m";
	}

}
