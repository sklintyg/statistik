package se.inera.statistics.core.api;

import java.util.Date;

public class PeriodResult {

	private Date start;
	private Date end;
	
	private int value;
	
	private String label;
	
	PeriodResult() {
	}
	
	public static PeriodResult newResult(Date date) {
		PeriodResult result = new PeriodResult();
		result.setStart(date);
		return result;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void increaseValue() {
		this.value += 1;
	}
}
