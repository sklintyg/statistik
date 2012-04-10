package se.inera.statistics.core.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsResult {
	
	private final int overAllTotal;
	private final int overAllMatches;

	private final StatisticsViewRange view;
	
	private List<PeriodResult> totals;
	private List<PeriodResult> matches;
	
	public StatisticsResult(final int overAllMatches, final int overAllTotal, final StatisticsViewRange view) {
		this.matches = new ArrayList<PeriodResult>();
		this.totals = new ArrayList<PeriodResult>();
		
		this.view = view;
		this.overAllMatches = overAllMatches;
		this.overAllTotal = overAllTotal;
	}
	
	public StatisticsViewRange getView() {
		return this.view;
	}

	public int getOverAllTotal() {
		return overAllTotal;
	}

	public int getOverAllMatches() {
		return overAllMatches;
	}
	
	public List<PeriodResult> getTotals() {
		return Collections.unmodifiableList(this.totals);
	}
	
	public void setTotals(final List<PeriodResult> totals) {
		this.totals = totals;
	}
	
	public List<PeriodResult> getMatches() {
		return Collections.unmodifiableList(this.matches);
	}
	
	public void setMatches(final List<PeriodResult> matches) {
		this.matches = matches;
	}
}
