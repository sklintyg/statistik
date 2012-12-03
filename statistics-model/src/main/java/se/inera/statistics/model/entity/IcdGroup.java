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

public class IcdGroup {

	private final String icd10RangeStart;
	
	private final String icd10RangeEnd;
	
	private final String chapter;
	
	private final String description;
	
	IcdGroup(String chapter, String icd10RangeStart, String icd10RangeEnd, String description){
		this.chapter = chapter;
		this.icd10RangeStart = icd10RangeStart;
		this.icd10RangeEnd = icd10RangeEnd;
		this.description = description;
	}

	public String getIcd10RangeStart() {
		return icd10RangeStart;
	}

	public String getIcd10RangeEnd() {
		return icd10RangeEnd;
	}

	public String getChapter() {
		return chapter;
	}

	public String getDescription() {
		return description;
	}

}
