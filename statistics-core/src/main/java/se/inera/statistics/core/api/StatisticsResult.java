package se.inera.statistics.core.api;

import java.util.Collections;
import java.util.List;

public class StatisticsResult {
	
	private final List<RowResult> matches;
	
	public StatisticsResult() {
		this.matches = Collections.emptyList();
	}
	
	public StatisticsResult(List<RowResult> matches) {
		this.matches = matches;
	}
	
	public List<RowResult> getMatches() {
		return Collections.unmodifiableList(this.matches);
	}	
}
