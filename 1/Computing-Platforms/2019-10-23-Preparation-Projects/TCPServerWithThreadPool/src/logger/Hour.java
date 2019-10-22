package logger;

public class Hour extends GreedyTimeIntervalUnit {

	@Override
	protected GreedyTimeIntervalUnit getDivisorUnit() {
		return new Minute();
	}

	@Override
	protected long getMillisConversion() {
		return getDivisorUnit().getMillisConversion() * 60;
	}

	@Override
	protected String getUnitName() {
		return "h";
	}

}
