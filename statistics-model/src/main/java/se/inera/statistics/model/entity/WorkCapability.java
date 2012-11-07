/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
