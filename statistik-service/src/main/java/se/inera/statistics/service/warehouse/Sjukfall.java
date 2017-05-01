/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.warehouse;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10RangeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

// CHECKSTYLE:OFF FinalClass
public class Sjukfall {
    // CHECKSTYLE:ON FinalClass
    public static final int MAX_GAP = 5;

    private int start;
    private int lan;
    private int end;
    private int kon;
    private int alder;
    private List<Integer> sjukskrivningsgrader = new ArrayList<>();
    private int sjukskrivningsgrad;
    private Set<Lakare> lakare = new HashSet<>();
    private Lakare lastLakare;
    private int[] enhets;
    private int realDays;
    private long firstIntygId;
    private List<Diagnos> diagnoses = new ArrayList<>();
    private Diagnos diagnos;
    private boolean enkelt;
    private int lastEnhet;

    private Sjukfall() {
    }

    public static Sjukfall create(SjukfallExtended extendedSjukfall) {
        final Sjukfall sjukfall = new Sjukfall();
        sjukfall.start = extendedSjukfall.getStart();
        sjukfall.end = extendedSjukfall.getEnd();
        sjukfall.diagnoses = toDiagnoses(extendedSjukfall.getAllDxs());
        sjukfall.diagnos = toDiagnos(extendedSjukfall.getLastDiagnosis());
        sjukfall.realDays = extendedSjukfall.getRealDays();
        sjukfall.kon = extendedSjukfall.getKonInt();
        sjukfall.alder = extendedSjukfall.getAlder();
        sjukfall.sjukskrivningsgrad = extendedSjukfall.getSjukskrivningsgrad();
        sjukfall.sjukskrivningsgrader = new ArrayList<>(extendedSjukfall.getSjukskrivningsgrads());
        sjukfall.lan = extendedSjukfall.getLan();
        sjukfall.lakare = extendedSjukfall.getLakare();
        sjukfall.lastLakare = extendedSjukfall.getLastLakare();
        sjukfall.enhets = toArray(extendedSjukfall.getEnhets());
        sjukfall.lastEnhet = extendedSjukfall.getLastEnhet();
        sjukfall.enkelt = extendedSjukfall.isEnkelt();
        sjukfall.firstIntygId = extendedSjukfall.getFirstIntygId();
        return sjukfall;
    }

    private static int[] toArray(Set<Integer> integers) {
        final int[] ints = new int[integers.size()];
        final ArrayList<Integer> integersList = new ArrayList<>(integers);
        for (int i = 0; i < integersList.size(); i++) {
            int nextInt = integersList.get(i);
            ints[i] = nextInt;
        }
        return ints;
    }

    private static List<Diagnos> toDiagnoses(List<SjukfallExtended.Diagnos> dxs) {
        final ArrayList<Diagnos> newDxs = new ArrayList<>();
        for (SjukfallExtended.Diagnos dx : dxs) {
            newDxs.add(toDiagnos(dx));
        }
        return newDxs;
    }

    private static Diagnos toDiagnos(SjukfallExtended.Diagnos dx) {
        return new Diagnos(dx.getDiagnoskapitel(), dx.getDiagnosavsnitt(), dx.getDiagnoskategori(), dx.getDiagnoskod());
    }

    public Kon getKon() {
        return Kon.byNumberRepresentation(kon);
    }

    @Override
    public String toString() {
        return "Sjukfall{"
                + "start=" + start
                + ", end=" + end
                + ", realDays=" + getRealDays()
                + '}';
    }

    public int getDiagnoskategori() {
        return diagnos.diagnoskategori;
    }

    public int getDiagnoskod() {
        return diagnos.diagnoskod;
    }

    public List<Integer> getIcd10CodeForTypes(List<Icd10RangeType> rangeTypes) {
        List<Integer> result = new ArrayList<>();
        for (Icd10RangeType rangeType : rangeTypes) {
            switch (rangeType) {
                case KAPITEL:
                    result.add(getDiagnoskapitel());
                    break;
                case AVSNITT:
                    result.add(getDiagnosavsnitt());
                    break;
                case KATEGORI:
                    result.add(getDiagnoskategori());
                    break;
                case KOD:
                    result.add(getDiagnoskod());
                    break;
                default: throw new UnknownRangeTypeException("Unknown range type: " + rangeTypes);
            }
        }
        return result;
    }

    public List<Integer> getAllIcd10OfTypes(List<Icd10RangeType> icd10RangeTypes) {
        List<Integer> result = new ArrayList<>();
        for (Icd10RangeType icd10RangeType : icd10RangeTypes) {
            for (Diagnos diagnose : diagnoses) {
                switch (icd10RangeType) {
                    case KATEGORI:
                        result.add(diagnose.diagnoskategori);
                        break;
                    case AVSNITT:
                        result.add(diagnose.diagnosavsnitt);
                        break;
                    case KAPITEL:
                        result.add(diagnose.diagnoskapitel);
                        break;
                    case KOD:
                        result.add(diagnose.diagnoskod);
                        break;
                    default: throw new UnknownRangeTypeException("Unknown icd range type: " + icd10RangeType);
                }
            }
        }
        return result;
    }

    public int getAlder() {
        return alder;
    }

    public int getRealDays() {
        return realDays;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDiagnoskapitel() {
        return diagnos.diagnoskapitel;
    }

    public Stream<Integer> getDiagnoskapitels() {
        return diagnoses.stream().map(d -> d.diagnoskapitel);
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public List<Integer> getSjukskrivningsgrader() {
        return sjukskrivningsgrader;
    }

    public int getDiagnosavsnitt() {
        return diagnos.diagnosavsnitt;
    }

    public String getLanskod() {
        return String.format("%1$02d", lan);
    }

    public Set<Lakare> getLakare() {
        return lakare;
    }

    public Lakare getLastLakare() {
        return lastLakare;
    }

    public int[] getEnhets() {
        return enhets;
    }

    public int getLastEnhet() {
        return lastEnhet;
    }

    public boolean isEnkelt() {
        return enkelt;
    }

    public long getFirstIntygId() {
        return firstIntygId;
    }

    private static final class Diagnos {
        private final int diagnoskapitel;
        private final int diagnosavsnitt;
        private final int diagnoskategori;
        private final int diagnoskod;

        private Diagnos(int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int diagnoskod) {
            this.diagnoskapitel = diagnoskapitel;
            this.diagnosavsnitt = diagnosavsnitt;
            this.diagnoskategori = diagnoskategori;
            this.diagnoskod = diagnoskod;
        }
    }

    private static class UnknownRangeTypeException extends RuntimeException {

        UnknownRangeTypeException(String s) {
            super(s);
        }
    }
}
