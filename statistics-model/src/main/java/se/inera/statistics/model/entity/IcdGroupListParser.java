package se.inera.statistics.model.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class IcdGroupListParser {

    private IcdGroupListParser() {
        // prevent instantiation
    }
    
    public static IcdGroupList parseUrl(URL url) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "ISO-8859-1"));

        try {
            IcdGroupList icdGroupList = new IcdGroupList();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                IcdGroup icdGroup = parse(line);
                icdGroupList.add(icdGroup);
            }
            return icdGroupList;
        } finally {
            bufferedReader.close();
        }
    }

    private static IcdGroup parse(String line) {
        final String[] tokens = line.split("\\t");
        final String[] icds = tokens[1].split("-");

        String icd10RangeStart = icds[0];
        String icd10RangeEnd = icds[1];
        String description = tokens[2];

        return new IcdGroup(tokens[0], icd10RangeStart, icd10RangeEnd, description);
    }

}
