package logger;

public class Day extends GreedyTimeIntervalUnit {

	@Override
	protected GreedyTimeIntervalUnit getDivisorUnit() {
		return new Hour();
	}

	@Override
	protected long getMillisConversion() {
		return getDivisorUnit().getMillisConversion() * 24;
	}

	@Override
	protected String getUnitName() {
		return "d";
	}

}
