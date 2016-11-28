/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.report.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import se.inera.statistics.service.report.model.Icd;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Icd10 {

    private static final Logger LOG = LoggerFactory.getLogger(Icd10.class);

    private static final int STARTINDEX_LAST_KATEGORI = 4;

    private static final int ENDINDEX_FIRST_KATEGORI = 3;

    private static final int ENDINDEX_ID = 7;
    private static final int STARTINDEX_DESCRIPTION = 7;
    private static final int MAX_CODE_LENGTH = 3;

    public static final String OTHER_KAPITEL = "Ö00-Ö00";
    public static final String OTHER_AVSNITT = "Ö00-Ö00";
    public static final String OTHER_KATEGORI = "Ö00";
    public static final String OTHER_KOD = "Ö000";
    private static final int INTID_OTHER_KATEGORI = Icd10.icd10ToInt(Icd10.OTHER_KATEGORI, Icd10RangeType.KATEGORI);
    private static final List<Integer> INTERNAL_ICD10_INTIDS = Arrays.asList(
                                                                        icd10ToInt(OTHER_KAPITEL, Icd10RangeType.KAPITEL),
                                                                        icd10ToInt(OTHER_AVSNITT, Icd10RangeType.AVSNITT),
                                                                        INTID_OTHER_KATEGORI,
                                                                        icd10ToInt(OTHER_KOD, Icd10RangeType.KOD));
    public static final String UNKNOWN_CODE_NAME = "Utan giltig ICD-10 kod";

    @Autowired
    private Resource icd10KategoriAnsiFile;

    @Autowired
    private Resource icd10AvsnittAnsiFile;

    @Autowired
    private Resource icd10KapitelAnsiFile;

    @Autowired
    private Resource icd10KodAnsiFile;

    @Autowired
    private Resource icd10VwxyKodAnsiFile;

    private List<Kapitel> kapitels;

    private List<Id> internalIcd10 = new ArrayList<>();

    private IdMap<Kategori> idToKategoriMap = new IdMap<>();
    private IdMap<Avsnitt> idToAvsnittMap = new IdMap<>();
    private IdMap<Kapitel> idToKapitelMap = new IdMap<>();
    private IdMap<Kod> idToKodMap = new IdMap<>();
    private IdMap<Id> idToInternalMap = new IdMap<>();
    private Map<Integer, ? extends Id> intIdMap;

    public static int icd10ToInt(String id, Icd10RangeType rangeType) {
        final String upperCaseId = id.toUpperCase().trim();
        int code = 0;
        final int firstCharMultiplier = 10_000_000;
        code += (upperCaseId.charAt(0) - '0' + 1) * firstCharMultiplier;

        final int secondCharMultiplier = 100_000;
        code += (upperCaseId.charAt(1) - '0' + 1) * secondCharMultiplier;

        final int thirdCharMultiplier = 10_000;
        code += (upperCaseId.charAt(2) - '0' + 1) * thirdCharMultiplier;

        final int three = 3;
        if (upperCaseId.length() > three) {
            final int fourthCharMultiplier = 1000;
            code += (upperCaseId.charAt(three) - '0' + 1) * fourthCharMultiplier;

            final int four = 4;
            if (upperCaseId.length() > four) {
                final int fifthCharMultiplier = 10;
                code += (upperCaseId.toUpperCase().charAt(four) - '0' + 1) * fifthCharMultiplier;
            }
        }

        final int rangeTypeMultiplier = 1;
        code += rangeType.ordinal() * rangeTypeMultiplier;

        return code;
    }

    @PostConstruct
    @java.lang.SuppressWarnings("squid:UnusedPrivateMethod") // Suppress Sonar warning. Method is invoked by Spring.
    private void init() {
        try {
            populateIdMap(icd10KapitelAnsiFile, idToKapitelMap, Kapitel::valueOf);
            populateIdMap(icd10AvsnittAnsiFile, idToAvsnittMap, s -> Avsnitt.valueOf(s, idToKapitelMap.values()));
            populateIdMap(icd10KategoriAnsiFile, idToKategoriMap, s -> Kategori.valueOf(s, idToAvsnittMap.values()));
            populateIdMap(icd10KodAnsiFile, idToKodMap, s -> Kod.valueOf(s, idToKategoriMap.values()));
            populateIdMap(icd10VwxyKodAnsiFile, idToKodMap, s -> Kod.valueOf(s, idToKategoriMap.values()));
            populateInternalIcd10();
            kapitels = new ArrayList<>(idToKapitelMap.values());
            intIdMap = Stream.of(idToKategoriMap.values(), idToAvsnittMap.values(), idToKapitelMap.values(), idToKodMap.values(), internalIcd10)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Id::toInt, java.util.function.Function.identity()));
        } catch (IOException e) {
            LOG.error("Could not parse ICD10: " + e);
        }
    }

    private <T extends Id> void populateIdMap(Resource file, IdMap<T> idMapToPopulate, Function<String, T> parseToRangeFunction) throws IOException {
        try (LineReader lr = new LineReader(file)) {
            String line;
            while ((line = lr.next()) != null) {
                T kapitel = parseToRangeFunction.apply(line);
                idMapToPopulate.put(kapitel);
            }
        }
    }

    private void populateInternalIcd10() {
        final Kapitel kapitel = new Kapitel(OTHER_KAPITEL, UNKNOWN_CODE_NAME);
        internalIcd10.add(kapitel);
        final Avsnitt avsnitt = new Avsnitt(OTHER_KAPITEL, UNKNOWN_CODE_NAME, kapitel);
        internalIcd10.add(avsnitt);
        final Kategori kategori = new Kategori(OTHER_KATEGORI, UNKNOWN_CODE_NAME, avsnitt);
        internalIcd10.add(kategori);
        internalIcd10.add(new Kod(OTHER_KOD, UNKNOWN_CODE_NAME, kategori));
        for (Id id : internalIcd10) {
            idToInternalMap.put(id);
        }
    }

    public List<Kapitel> getKapitel(boolean includeInternalKapitel) {
        final ArrayList<Kapitel> allKapitels = new ArrayList<>(kapitels);
        if (includeInternalKapitel) {
            allKapitels.addAll(Collections2.transform(Collections2.filter(internalIcd10, new Predicate<Id>() {
                @Override
                public boolean apply(Id id) {
                    return id instanceof Kapitel;
                }
            }), new Function<Id, Kapitel>() {
                @Override
                public Kapitel apply(Id id) {
                    return (Kapitel) id;
                }
            }));
        }
        return allKapitels;
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

    public List<Icd> getIcdStructure() {
        List<Icd10.Kapitel> kapitel = getKapitel(false);
        final List<Icd> icds = new ArrayList<>(Lists.transform(kapitel, new Function<Icd10.Kapitel, Icd>() {
            @Override
            public Icd apply(Icd10.Kapitel kapitel) {
                return new Icd(kapitel, Kategori.class);
            }
        }));
        icds.add(new Icd("", "Utan giltig ICD-10 kod", INTID_OTHER_KATEGORI));
        return icds;
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

    public Kod getKod(String diagnoskod) {
        return idToKodMap.get(diagnoskod);
    }

    public Kategori findKategori(String rawCode) {
        String normalized = normalize(rawCode);
        return getKategori(normalized);
    }

    public Kod findKod(String rawCode) {
        final String normalized = rawCode.toUpperCase(Locale.ENGLISH).replaceAll("[^A-Z0-9]", "");
        final Kod kod = getKod(normalized);
        if (kod == null && !normalized.isEmpty()) {
            return findKod(normalized.substring(0, normalized.length() - 1));
        }
        return kod;
    }

    public Id findIcd10FromNumericId(int numId) {
        final Id id = intIdMap.get(numId);
        if (id == null) {
            throw new Icd10NotFoundException("ICD10 with numerical id could not be found: " + numId);
        }
        return id;
    }

    public static List<Integer> getKapitelIntIds(String... icdIds) {
        return Lists.transform(Arrays.asList(icdIds), icdId -> icd10ToInt(icdId, Icd10RangeType.KAPITEL));
    }

    public Id findFromIcd10Code(String icd10) {
        return Stream
                .of(idToKategoriMap, idToAvsnittMap, idToKapitelMap, idToKodMap, idToInternalMap)
                .filter(idMap -> idMap.containsKey(icd10))
                .findAny()
                .map((java.util.function.Function<IdMap<? extends Id>, Id>) idMap -> idMap.get(icd10))
                .orElseThrow((Supplier<RuntimeException>) () -> new Icd10NotFoundException("ICD10 with id could not be found: " + icd10));
    }

    private static <T extends Range> T find(String id, Collection<T> ranges) {
        for (T range: ranges) {
            if (range.contains(id)) {
                return range;
            }
        }
        return null;
    }

    public static class IdMap<T extends Id> extends HashMap<String, T> {
        public void put(T id) {
            if (id != null) {
                put(id.getId(), id);
            }
        }
    }

    public abstract static class Id {
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

        public abstract int toInt();

        public abstract List<Id> getSubItems();

        public abstract Optional<Id> getParent();

        public String getVisibleId() {
            return isInternal() ? "" : getId();
        }

        public boolean isInternal() {
            return INTERNAL_ICD10_INTIDS.contains(toInt());
        }
    }

    public abstract static class Range extends Id {

        public Range(String id, String name) {
            super(id, name);
        }

        abstract boolean contains(String id);

    }

    public abstract static class RangeType extends Range {
        private final String firstId;
        private final String lastId;

        public RangeType(String range, String name) {
            super(range, name);
            firstId = range.substring(0, ENDINDEX_FIRST_KATEGORI);
            lastId = range.substring(STARTINDEX_LAST_KATEGORI);
        }

        @Override
        public boolean contains(String kategoriId) {
            return firstId.compareTo(kategoriId) <= 0 && lastId.compareTo(kategoriId) >= 0;
        }
    }

    public static class Kapitel extends RangeType {

        private final List<Avsnitt> avsnitt = new ArrayList<>();
        private final int intId;

        public Kapitel(String range, String name) {
            super(range.toUpperCase(), name);
            intId = icd10ToInt(getId(), Icd10RangeType.KAPITEL);
        }

        @Override
        public List<Id> getSubItems() {
            return getAvsnitt().stream().map(avs -> (Id) avs).collect(Collectors.toList());
        }

        @Override
        public Optional<Id> getParent() {
            return Optional.absent();
        }

        public static Kapitel valueOf(String line) {
            return new Kapitel(line.substring(0, ENDINDEX_ID), line.substring(STARTINDEX_DESCRIPTION));
        }

        public List<Avsnitt> getAvsnitt() {
            return avsnitt;
        }

        @Override
        public int toInt() {
            return intId;
        }

    }

    public static class Avsnitt extends RangeType {

        private final Kapitel kapitel;
        private final List<Kategori> kategori;
        private final int intId;

        public Avsnitt(String range, String name, Kapitel kapitel) {
            super(range.toUpperCase(), name);
            this.kapitel = kapitel;
            kategori = new ArrayList<>();
            kapitel.avsnitt.add(this);
            intId = icd10ToInt(getId(), Icd10RangeType.AVSNITT);
        }

        public static Avsnitt valueOf(String line, Collection<Kapitel> kapitels) {
            String id = line.substring(0, ENDINDEX_ID);
            Kapitel kapitel = find(id.substring(0, ENDINDEX_FIRST_KATEGORI), kapitels);
            if (kapitel == null) {
                return null;
            }

            return new Avsnitt(id, line.substring(STARTINDEX_DESCRIPTION), kapitel);
        }

        public List<Kategori> getKategori() {
            return kategori;
        }

        public Kapitel getKapitel() {
            return kapitel;
        }

        @Override
        public Optional<Id> getParent() {
            return Optional.of(getKapitel());
        }

        @Override
        public List<Id> getSubItems() {
            return getKategori().stream().map(kat -> (Id) kat).collect(Collectors.toList());
        }

        @Override
        public int toInt() {
            return intId;
        }

    }

    public static class Kategori extends Range {

        private final Avsnitt avsnitt;
        private final List<Kod> kods;
        private final int intId;

        public Kategori(String id, String name, Avsnitt avsnitt) {
            super(id.toUpperCase(), name);
            this.avsnitt = avsnitt;
            this.kods = new ArrayList<>();
            avsnitt.kategori.add(this);
            intId = icd10ToInt(getId(), Icd10RangeType.KATEGORI);
        }

        public static Kategori valueOf(String line, Collection<Avsnitt> avsnitts) {
            String id = line.substring(0, ENDINDEX_FIRST_KATEGORI);
            Avsnitt avsnitt = find(id, avsnitts);
            if (avsnitt == null) {
                return null;
            }
            return new Kategori(id, line.substring(STARTINDEX_DESCRIPTION), avsnitt);
        }

        public Avsnitt getAvsnitt() {
            return avsnitt;
        }

        public List<Kod> getKods() {
            return kods;
        }

        @Override
        public List<Id> getSubItems() {
            return getKods().stream().map(kod -> (Id) kod).collect(Collectors.toList());
        }

        @Override
        public Optional<Id> getParent() {
            return Optional.of(getAvsnitt());
        }

        @Override
        public int toInt() {
            return intId;
        }

        @Override
        public boolean contains(String kodId) {
            return this.getId().equals(kodId.substring(0, MAX_CODE_LENGTH));
        }


    }

    public static class Kod extends Id {

        private final Kategori kategori;
        private final int intId;

        public Kod(String id, String name, Kategori kategori) {
            super(id.toUpperCase(), name);
            this.kategori = kategori;
            kategori.kods.add(this);
            intId = icd10ToInt(getId(), Icd10RangeType.KOD);
        }

        public static Kod valueOf(String line, Collection<Kategori> kategoris) {
            String id = line.replaceAll("\\s.*", "");
            Kategori kategori = find(id, kategoris);
            if (kategori == null) {
                return null;
            }
            return new Kod(id, line.replaceAll("^[^\\s]*\\s*", ""), kategori);
        }

        public Kategori getKategori() {
            return kategori;
        }

        @Override
        public Optional<Id> getParent() {
            return Optional.of(getKategori());
        }

        @Override
        public List<Id> getSubItems() {
            return Collections.emptyList();
        }

        @Override
        public int toInt() {
            return intId;
        }

    }

    private static class LineReader implements Closeable {
        private final BufferedReader reader;
        LineReader(Resource resource) throws IOException {
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

    private class Icd10NotFoundException extends RuntimeException {

        Icd10NotFoundException(String s) {
            super(s);
        }

    }

}
