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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IcdGroupList {

	private static final Logger LOG = LoggerFactory.getLogger(IcdGroupList.class);
	
	private List<IcdGroup> mapping = new ArrayList<IcdGroup>();
	private final IcdGroup unknownIcd;
	
	public IcdGroupList() {
		 unknownIcd = new IcdGroup("Okänd", null, null, "Okända ICDtexter");
	}
	
	public void setMappings(URL url) throws IOException {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
		String line;
		
		while ((line = bufferedReader.readLine()) != null){
			final String[] tokens = line.split("\\t");
			final String[] icds = tokens[1].split("-");
			
			String icd10RangeStart = icds[0];
			String icd10RangeEnd = icds[1];
			String description = tokens[2];
			
			mapping.add(new IcdGroup(tokens[0], icd10RangeStart, icd10RangeEnd, description));
		}
		bufferedReader.close();		
	}
	
	public IcdGroup getGroup(String icd10) {
		if (icd10 != null && icd10.length() >=3) {
			final String icd10FirstThreeCharacters = icd10.substring(0, 3);
			for (IcdGroup group : mapping){
				if (inRange(icd10FirstThreeCharacters, group)) {
					return group;
				}
			}
		}
		
		LOG.warn("Could not find icd code '{0}'", icd10);
		return unknownIcd;
		
	}

	private boolean inRange(final String icd10FirstThreeCharacters, IcdGroup group) {
		return group.getIcd10RangeStart().compareTo(icd10FirstThreeCharacters) <= 0 && group.getIcd10RangeEnd().compareTo(icd10FirstThreeCharacters) >= 0;
	}
}