package se.inera.statistics.core.api;

public class RowResult {

	private	final String xValue;
	private int yValue1;
	private int yValue2;
	
	RowResult(String xValue) {
		this.xValue = xValue;
	}
	
	public static RowResult newResult(String x, int y1, int y2) {
		RowResult result = new RowResult(x);
		result.setyValue1(y1);
		result.setyValue2(y2);

		return result;
	}

	public String getxValue() {
		return xValue;
	}

	public int getyValue1() {
		return yValue1;
	}

	public void setyValue1(int yValue1) {
		this.yValue1 = yValue1;
	}

	public int getyValue2() {
		return yValue2;
	}

	public void setyValue2(int yValue2) {
		this.yValue2 = yValue2;
	}

}
