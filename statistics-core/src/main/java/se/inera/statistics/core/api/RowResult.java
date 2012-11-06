/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
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
