package se.inera.statistics.core.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticsResult {
	
	private List<RowResult> matches;
	
	public StatisticsResult() {
		this.matches = new ArrayList<RowResult>();
	}
	
	public List<RowResult> getMatches() {
		return Collections.unmodifiableList(this.matches);
	}
	
	public void setMatches(final List<RowResult> matches) {
		this.matches = matches;
	}
}
