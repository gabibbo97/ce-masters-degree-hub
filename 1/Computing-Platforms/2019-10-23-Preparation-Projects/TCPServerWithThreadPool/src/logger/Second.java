package logger;

public class Second extends GreedyTimeIntervalUnit {

	@Override
	protected GreedyTimeIntervalUnit getDivisorUnit() {
		return new Millisecond();
	}

	@Override
	protected long getMillisConversion() {
		return getDivisorUnit().getMillisConversion() * 1000;
	}

	@Override
	protected String getUnitName() {
		return "s";
	}

}
