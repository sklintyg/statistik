package se.inera.statistics.service.report.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Icd10 {

    private static final Logger LOG = LoggerFactory.getLogger(Icd10.class);

    private static final int STARTINDEX_LAST_KATEGORI = 4;

    private static final int ENDINDEX_FIRST_KATEGORI = 3;

    private static final int ENDINDEX_ID = 7;
    private static final int STARTINDEX_DESCRIPTION = 7;
    public static final int MAX_CODE_LENGTH = 3;

    @Autowired
    private Resource icd10KategoriAnsiFile;

    @Autowired
    private Resource icd10AvsnittAnsiFile;

    @Autowired
    private Resource icd10KapitelAnsiFile;

    private List<Kapitel> kapitels;

    private IdMap<Kategori> idToKategoriMap = new IdMap<>();
    private IdMap<Avsnitt> idToAvsnittMap = new IdMap<>();
    private IdMap<Kapitel> idToKapitelMap = new IdMap<>();

    @PostConstruct
    private void init() {
        try {
            kapitels = new ArrayList<>();
            try (LineReader lr = new LineReader(icd10KapitelAnsiFile)) {
                String line;
                while ((line = lr.next()) != null) {
                    Kapitel kapitel = Kapitel.valueOf(line);
                    kapitels.add(kapitel);
                    idToKapitelMap.put(kapitel);
                }
            }

            try (LineReader lr = new LineReader(icd10AvsnittAnsiFile)) {
                String line;
                while ((line = lr.next()) != null) {
                    Avsnitt avsnitt = Avsnitt.valueOf(line, idToKapitelMap.values());
                    idToAvsnittMap.put(avsnitt);
                }
            }

            try (LineReader lr = new LineReader(icd10KategoriAnsiFile)) {
                String line;
                while ((line = lr.next()) != null) {
                    Kategori kategori = Kategori.valueOf(line, idToAvsnittMap.values());
                    idToKategoriMap.put(kategori);
                }
            }

        } catch (IOException e) {
            LOG.error("Could not parse ICD10: " + e);
        }
    }

    public List<Kapitel> getKapitel() {
        return kapitels;
    }

    static String normalize(String icd10Code) {
        StringBuilder normalized = new StringBuilder(icd10Code.length());
        for (char c : icd10Code.toUpperCase().toCharArray()) {
            if ('A' <= c && c <= 'Z' || '0' <= c && c <= '9') {
                normalized.append(c);
            }
        }

        if (normalized.length() > MAX_CODE_LENGTH) {
            normalized.setLength(MAX_CODE_LENGTH);
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

    public Kategori findKategori(String rawCode) {
        String normalized = normalize(rawCode);
        return getKategori(normalized);
    }

    public static class IdMap<T extends Id> extends HashMap<String, T> {
        public void put(T id) {
            if (id != null) {
                put(id.getId(), id);
            }
        }
    }

    public static class Id {
        private final String id;
        private final String name;

        public Id(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        // CHECKSTYLE:OFF MagicNumber
        public int toInt() {
            return (id.charAt(0) - 'A') * 100 + (id.charAt(1) - '0') * 10 + id.charAt(2) - '0';
        }
        // CHECKSTYLE:ON MagicNumber
    }

    public static class Range extends Id {
        private final String firstId;
        private final String lastId;

        public Range(String range, String name) {
            super(range, name);
            firstId = range.substring(0, ENDINDEX_FIRST_KATEGORI);
            lastId = range.substring(STARTINDEX_LAST_KATEGORI);
        }

        public boolean contains(String kategoriId) {
            return firstId.compareTo(kategoriId) <= 0 && lastId.compareTo(kategoriId) >= 0;
        }
    }

    public static class Kapitel extends Range {

        private final List<Avsnitt> avsnitt = new ArrayList<>();

        public Kapitel(String range, String name) {
            super(range.toUpperCase(), name);
        }

        public static Kapitel valueOf(String line) {
            return new Kapitel(line.substring(0, ENDINDEX_ID), line.substring(STARTINDEX_DESCRIPTION));
        }

        public List<Avsnitt> getAvsnitt() {
            return avsnitt;
        }

    }

    public static class Avsnitt extends Range {

        private final Kapitel kapitel;
        private final List<Kategori> kategori;

        public Avsnitt(String range, String name, Kapitel kapitel) {
            super(range.toUpperCase(), name);
            this.kapitel = kapitel;
            kategori = new ArrayList<>();
        }

        public static Avsnitt valueOf(String line, Collection<Kapitel> kapitels) {
            String id = line.substring(0, ENDINDEX_ID);
            Kapitel kapitel = find(id.substring(0, ENDINDEX_FIRST_KATEGORI), kapitels);
            if (kapitel == null) {
                return null;
            }

            Avsnitt avsnitt = new Avsnitt(id, line.substring(STARTINDEX_DESCRIPTION), kapitel);
            kapitel.avsnitt.add(avsnitt);
            return avsnitt;
        }

        private static Kapitel find(String id, Collection<Kapitel> kapitels) {
            for (Kapitel kapitel: kapitels) {
                if (kapitel.contains(id)) {
                    return kapitel;
                }
            }
            return null;
        }

        public List<Kategori> getKategori() {
            return kategori;
        }

        public Kapitel getKapitel() {
            return kapitel;
        }
    }

    public static class Kategori extends Id {

        private final Avsnitt avsnitt;

        public Kategori(String id, String name, Avsnitt avsnitt) {
            super(id.toUpperCase(), name);
            this.avsnitt = avsnitt;
        }

        public static Kategori valueOf(String line, Collection<Avsnitt> avsnitts) {
            String id = line.substring(0, ENDINDEX_FIRST_KATEGORI);
            Avsnitt avsnitt = find(id, avsnitts);
            if (avsnitt == null) {
                return null;
            }
            Kategori kategori = new Kategori(id, line.substring(STARTINDEX_DESCRIPTION), avsnitt);
            avsnitt.kategori.add(kategori);
            return kategori;
        }

        private static Avsnitt find(String id, Collection<Avsnitt> avsnitts) {
            for (Avsnitt avsnitt: avsnitts) {
                if (avsnitt.contains(id)) {
                    return avsnitt;
                }
            }
            return null;
        }

        public Avsnitt getAvsnitt() {
            return avsnitt;
        }
    }

    private static class LineReader implements Closeable {
        private final BufferedReader reader;
        public LineReader(Resource resource) throws IOException {
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "ISO-8859-1"));
        }

        public String next() throws IOException {
            return reader.readLine();
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
