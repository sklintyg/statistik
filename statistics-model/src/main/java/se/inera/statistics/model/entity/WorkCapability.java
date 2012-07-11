package se.inera.statistics.model.entity;

import java.security.InvalidParameterException;

public enum WorkCapability {

	NO_WORKING_CAPABILITY(0),
	ONE_QUARTER_WORKING_CAPABILITY(1),
	HALF_WORKING_CAPABILITY(2),
	THREE_QUARTER_WORKING_CAPABILITY(3),
	FULL_WORKING_CAPABILITY(4);
	
	private final int code;
	
	private WorkCapability(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static WorkCapability fromWorkDisabilityPercentage(Integer workingDisabilityPercentage){
		switch(workingDisabilityPercentage) {
		case 100:
			return WorkCapability.NO_WORKING_CAPABILITY;
		case 75:
			return WorkCapability.ONE_QUARTER_WORKING_CAPABILITY;
		case 50:
			return WorkCapability.HALF_WORKING_CAPABILITY;
		case 25:
			return WorkCapability.THREE_QUARTER_WORKING_CAPABILITY;
		case 0:
			return WorkCapability.FULL_WORKING_CAPABILITY;
		}
		throw new InvalidParameterException("Work capability is out of range. " + workingDisabilityPercentage + " is not a valid percentage. Values allowed are," +
				" 0, 25, 50, 75, 100");
	}
}
