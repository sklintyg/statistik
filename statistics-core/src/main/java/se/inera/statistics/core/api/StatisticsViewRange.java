/**
 * Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.core.api;

import java.util.EnumSet;

public enum StatisticsViewRange {
	DAILY(0),
	WEEKLY(1),
	MONTHLY(2),
	YEARLY(3);
	
	private final int code;
	
	private StatisticsViewRange(final int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static StatisticsViewRange fromCode(final int code) {
		for (final StatisticsViewRange vr : EnumSet.allOf(StatisticsViewRange.class)) {
			if (vr.getCode() == code) {
				return vr;
			}
		}
		
		throw new IllegalArgumentException("Code: " + code + " not present in enum " + StatisticsViewRange.class.getSimpleName());
	}
}
