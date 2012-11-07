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
package se.inera.statistics.rest;

public class MonthwiseForm {

	private String from;
	String to;
	String disability;
	String group;
	
	public MonthwiseForm() {		
	}
	
	public String getFromDate() {
		return from;
	}
	
	public void setFromDate(String from) {
		this.from = from;
	}
	
	public String getToDate() {
		return to;
	}
	
	public void setToDate(String to) {
		this.to = to;
	}
	
	public String getDisability() {
		return disability;
	}
	
	public void setDisability(String disability) {
		this.disability = disability;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	
}
