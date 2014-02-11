package se.inera.statistics.service.report.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class Icd10 {

    @Autowired
    private Resource icd10KategoriAnsiFile;

    @Autowired
    private Resource icd10AvsnittAnsiFile;

    @Autowired
    private Resource icd10KapitelAnsiFile;

    private List<Kapitel> kapitel;

    private List<Avsnitt> avsnitt;

    private List<Kategori> kategori;

    @PostConstruct
    private void init() {
        try {

            kapitel = new LineReader<Kapitel>(icd10KapitelAnsiFile) {
                public Kapitel parse(String line) {
                    return Kapitel.valueOf(line);
                }
            }.process();

            avsnitt = new LineReader<Avsnitt>(icd10AvsnittAnsiFile) {
                public Avsnitt parse(String line) {
                    return Avsnitt.valueOf(line);
                }
            }.process();

            for (Avsnitt a: avsnitt) {
                String aid = a.getId().substring(0, 3);
                for (Kapitel k: kapitel) {
                    String kid = k.getId().substring(4);
                    if (kid.compareTo(aid) >= 0) {
                        k.avsnitt.add(a);
                        break;
                    }
                }
            }
            kategori = new LineReader<Kategori>(icd10KategoriAnsiFile) {
                public Kategori parse(String line) {
                    return Kategori.valueOf(line);
                }
            }.process();

            for (Kategori k: kategori) {
                String kid = k.getId().substring(0, 3);
                for (Avsnitt a: avsnitt) {
                    String aid = a.getId().substring(4);
                    if (kid.compareTo(aid) <= 0) {
                        a.kategori.add(k);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Kapitel> getKapitel() {
        return kapitel;
    }

    public List<Avsnitt> getAvsnitt() {
        return avsnitt;
    }

    public static class Kapitel {

        private final String id;
        private final String name;
        private final List<Avsnitt> avsnitt = new ArrayList<>();

        public Kapitel(String range, String name) {
            this.id = range;
            this.name = name;
        }

        public static Kapitel valueOf(String line) {
            return new Kapitel(line.substring(0, 7), line.substring(7));
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
    }

    public static class Avsnitt {

        private final String id;
        private final String name;
        private final List<Kategori> kategori;
        
        public Avsnitt(String range, String name) {
            this.id = range;
            this.name = name;
            kategori = new ArrayList<>();
        }

        public static Avsnitt valueOf(String line) {
            return new Avsnitt(line.substring(0, 7), line.substring(7));
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
    }

    public static class Kategori {

        private final String id;
        private final String name;

        public Kategori(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Kategori valueOf(String line) {
            return new Kategori(line.substring(0, 3), line.substring(7));
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }
    }
    
    private static abstract class LineReader<T> {
        
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
