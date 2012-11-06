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
package se.inera.statistics.model.entity;

public class IcdGroup {

	private String icd10RangeStart;
	
	private String icd10RangeEnd;
	
	private String chapter;
	
	private String Description;
	
	IcdGroup(){
	}

	public String getIcd10RangeStart() {
		return icd10RangeStart;
	}

	public void setIcd10RangeStart(String icd10RangeStart) {
		this.icd10RangeStart = icd10RangeStart;
	}

	public String getIcd10RangeEnd() {
		return icd10RangeEnd;
	}

	public void setIcd10RangeEnd(String icd10RangeEnd) {
		this.icd10RangeEnd = icd10RangeEnd;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
}
