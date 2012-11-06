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
package se.inera.statistics.model.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IcdGroupList {

	private List<IcdGroup> mapping = new ArrayList<IcdGroup>();
	
	public void setMappings(URL url) throws IOException {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
		String line = null;
		
		while (null != (line = bufferedReader.readLine())){
			final IcdGroup icdGroup = new IcdGroup();
			final String[] tokens = line.split("\\t");
			final String[] icds = tokens[1].split("-");
			
			icdGroup.setChapter(tokens[0]);
			icdGroup.setIcd10RangeStart(icds[0]);
			icdGroup.setIcd10RangeEnd(icds[1]);
			icdGroup.setDescription(tokens[2]);

			mapping.add(icdGroup);
		}
		bufferedReader.close();
	}
	
	public IcdGroup getGroup(String icd10) {
		//TODO: a better search mechanism
		final String icd10FirstThreeCharacters = icd10.substring(0, 3);
		for (IcdGroup group : mapping){
			if (group.getIcd10RangeStart().compareTo(icd10FirstThreeCharacters) <= 0) {
				if (group.getIcd10RangeEnd().compareTo(icd10FirstThreeCharacters) >= 0) {
					return group;
				}
			}
		}
		throw new RuntimeException("Could not find icd code '" + icd10 + "' . Illegal state!");
	}
}