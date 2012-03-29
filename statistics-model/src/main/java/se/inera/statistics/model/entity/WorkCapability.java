package se.inera.statistics.model.entity;

public enum WorkCapability {

	NO_WORKING_CAPABILITY(0),
	ONE_QUARTER_WORKING_CAPABILITY(1),
	HALF_WORKING_CAPABILITY(2),
	THREE_QUARTER_WORKING_CAPABILITY(3);
	
	private final int code;
	
	private WorkCapability(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
}
