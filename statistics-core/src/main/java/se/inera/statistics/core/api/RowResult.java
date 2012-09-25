package se.inera.statistics.core.api;

public class RowResult {

	private	final String xValue;
	private final long yValue1;
	private final long yValue2;
	
	RowResult(String xValue, long y1, long y2) {
		this.xValue = xValue;
		this.yValue1 = y1;
		this.yValue2 = y2;
	}
	
	public static RowResult newResult(String x, long y1, long y2) {
		return new RowResult(x, y1, y2);
	}

	public String getxValue() {
		return xValue;
	}

	public long getyValue1() {
		return yValue1;
	}

	public long getyValue2() {
		return yValue2;
	}

}
