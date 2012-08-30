package se.inera.statistics.model.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IcdGroupList {

	private List<IcdGroup> mapping = new ArrayList<IcdGroup>();
	
	public void setMappings(URL url) throws IOException {
		System.err.println(url);
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));
		String line = null;
		
		while (null != (line = bufferedReader.readLine())){
//			System.err.println(line);
			final IcdGroup icdGroup = new IcdGroup();
			final String[] tokens = line.split("\\t");
//			System.err.println(tokens[1] + tokens[2]);
			final String[] icds = tokens[1].split("-");
//			System.err.println(icds[0] + icds[1]);
			
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
//		System.err.println("Using " + icd10 + " -> " + icd10FirstThreeCharacters);
		for (IcdGroup group : mapping){
//			System.err.println("matching " + icd10FirstThreeCharacters + " against " + group.getIcd10RangeStart() + " " + group.getIcd10RangeEnd());
			if (group.getIcd10RangeStart().compareTo(icd10FirstThreeCharacters) <= 0) {
//				System.err.println("foudnd matching start");
				if (group.getIcd10RangeEnd().compareTo(icd10FirstThreeCharacters) >= 0) {
//					System.err.println("Found matching end");
					return group;
				}
			}
		}
		throw new RuntimeException("Could not find icd code. Illegal state!");
	}
}