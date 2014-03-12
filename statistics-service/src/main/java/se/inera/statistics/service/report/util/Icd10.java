package se.inera.statistics.service.report.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import se.inera.statistics.service.hsa.HSAStore;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Icd10 {

    private static final Logger LOG = LoggerFactory.getLogger(Icd10.class);

    private static final int STARTINDEX_LAST_KATEGORI = 4;

    private static final int ENDINDEX_FIRST_KATEGORI = 3;

    private static final int ENDINDEX_ID = 7;
    private static final int STARTINDEX_DESCRIPTION = 7;

    @Autowired
    private Resource icd10KategoriAnsiFile;

    @Autowired
    private Resource icd10AvsnittAnsiFile;

    @Autowired
    private Resource icd10KapitelAnsiFile;

    private List<Kapitel> kapitels;

    private List<Avsnitt> avsnitts;

    private Map<String, Kategori> idToKategoriMap = new HashMap<>();
    private Map<String, Avsnitt> idToAvsnittMap = new HashMap<>();
    private Map<String, Kapitel> idToKapitelMap = new HashMap<>();

    @PostConstruct
    private void init() {
        try {

            kapitels = (new LineReader<Kapitel>(icd10KapitelAnsiFile) {
                public Kapitel parse(String line) {
                    return Kapitel.valueOf(line);
                }
            }).process();

            avsnitts = (new LineReader<Avsnitt>(icd10AvsnittAnsiFile) {
                public Avsnitt parse(String line) {
                    return Avsnitt.valueOf(line);
                }
            }).process();

            List<Kategori> kategoris = (new LineReader<Kategori>(icd10KategoriAnsiFile) {
                public Kategori parse(String line) {
                    return Kategori.valueOf(line);
                }
            }).process();

            for (Avsnitt avsnitt : avsnitts) {
                String aid = firstKategori(avsnitt.getId());
                for (Kapitel k : kapitels) {
                    String kid = lastKategori(k.getId());
                    if (kid.compareTo(aid) >= 0) {
                        k.avsnitt.add(avsnitt);
                        avsnitt.setKapitel(k);
                        break;
                    }
                }
            }

            for (Kategori kategori : kategoris) {
                String kid = firstKategori(kategori.getId());
                for (Avsnitt a : avsnitts) {
                    String aid = lastKategori(a.getId());
                    if (kid.compareTo(aid) <= 0) {
                        a.kategori.add(kategori);
                        kategori.setAvsnitt(a);
                        break;
                    }
                }
            }

            for (Kategori kategori : kategoris) {
                idToKategoriMap.put(kategori.getId(), kategori);
            }

            for (Kapitel kapitel : kapitels) {
                idToKapitelMap.put(kapitel.getId(), kapitel);
            }

            for (Avsnitt avsnitt : avsnitts) {
                idToAvsnittMap.put(avsnitt.getId(), avsnitt);
            }

        } catch (IOException e) {
            LOG.error("Could not parse ICD10: " + e);
        }
    }

    private String lastKategori(String s) {
        return s.substring(STARTINDEX_LAST_KATEGORI);
    }

    private String firstKategori(String s) {
        return s.substring(0, ENDINDEX_FIRST_KATEGORI);
    }

    public List<Kapitel> getKapitel() {
        return kapitels;
    }

    public List<Avsnitt> getAvsnitt() {
        return avsnitts;
    }

    public String normalize(String icd10Code) {
        StringBuilder normalized = new StringBuilder(icd10Code.length());
        for (char c : icd10Code.toUpperCase().toCharArray()) {
            if ('A' <= c && c <= 'Z' || '0' <= c && c <= '9') {
                normalized.append(c);
            }
        }
        return normalized.toString();
    }

    public Kategori getKategori(String diagnoskategori) {
        return idToKategoriMap.get(diagnoskategori);
    }

    public Avsnitt getAvsnitt(String diagnosavsnitt) {
        return idToAvsnittMap.get(diagnosavsnitt);
    }

    public Kapitel getKapitel(String diagnoskapitel) {
        return idToKapitelMap.get(diagnoskapitel);
    }

    public static class Kapitel {

        private final int index;
        private final String id;
        private final String name;
        private final List<Avsnitt> avsnitt = new ArrayList<>();
        private static int indexcounter;

        public Kapitel(String range, String name) {
            this.index = indexcounter++;
            this.id = range;
            this.name = name;
        }

        public static Kapitel valueOf(String line) {
            return new Kapitel(line.substring(0, ENDINDEX_ID), line.substring(STARTINDEX_DESCRIPTION));
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public List<Avsnitt> getAvsnitt() {
            return avsnitt;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Avsnitt {

        private final String id;
        private final String name;
        private final List<Kategori> kategori;
        private static int indexcounter;
        private final int index;
        private Kapitel kapitel;

        public Avsnitt(String range, String name) {
            this.id = range;
            this.name = name;
            kategori = new ArrayList<>();
            this.index = indexcounter++;
        }

        public static Avsnitt valueOf(String line) {
            return new Avsnitt(line.substring(0, ENDINDEX_ID), line.substring(STARTINDEX_DESCRIPTION));
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public List<Kategori> getKategori() {
            return kategori;
        }

        public int getIndex() {
            return index;
        }

        public void setKapitel(Kapitel kapitel) {
            this.kapitel = kapitel;
        }

        public Kapitel getKapitel() {
            return kapitel;
        }
    }

    public static class Kategori {

        private final String id;
        private final String name;
        private static int indexcounter;
        private final int index;
        private Avsnitt avsnitt;

        public Kategori(String id, String name) {
            this.id = id;
            this.name = name;
            this.index = indexcounter++;
        }

        public static Kategori valueOf(String line) {
            return new Kategori(line.substring(0, ENDINDEX_FIRST_KATEGORI), line.substring(STARTINDEX_DESCRIPTION));
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public int getIndex() {
            return index;
        }

        public void setAvsnitt(Avsnitt avsnitt) {
            this.avsnitt = avsnitt;
        }

        public Avsnitt getAvsnitt() {
            return avsnitt;
        }
    }

    private abstract static class LineReader<T> {
        private final Resource resource;
        private final List<T> parsed;

        public LineReader(Resource resource) {
            this.resource = resource;
            this.parsed = new ArrayList<>();
        }

        public List<T> process() throws IOException {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "ISO-8859-1"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    T t = parse(line);
                    if (t != null) {
                        parsed.add(t);
                    }
                }
            }
            return parsed;
        }

        public abstract T parse(String line);
    }
}
