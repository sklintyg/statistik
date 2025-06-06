/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.service.warehouse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10RangeType;

// CHECKSTYLE:OFF FinalClass
public class Sjukfall implements Serializable {

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
    private Collection<HsaIdEnhet> enhets;
    private Collection<HsaIdEnhet> vardenhets;
    private int realDays;
    private long firstIntygId;
    private int intygCountIncludingBeforeCurrentPeriod;
    private List<Diagnos> diagnoses = new ArrayList<>();
    private Diagnos diagnos;
    private HsaIdEnhet lastEnhet;
    private HsaIdEnhet lastVardEnhet;

    private Sjukfall() {
    }

    public static Sjukfall create(SjukfallExtended extendedSjukfall) {
        final Sjukfall sjukfall = new Sjukfall();
        sjukfall.start = extendedSjukfall.getStart();
        sjukfall.end = extendedSjukfall.getEnd();
        sjukfall.diagnoses = extendedSjukfall.getAllDxs();
        sjukfall.diagnos = extendedSjukfall.getLastDiagnosis();
        sjukfall.realDays = extendedSjukfall.getRealDays();
        sjukfall.kon = extendedSjukfall.getKonInt();
        sjukfall.alder = extendedSjukfall.getAlder();
        sjukfall.sjukskrivningsgrad = extendedSjukfall.getSjukskrivningsgrad();
        sjukfall.sjukskrivningsgrader = new ArrayList<>(extendedSjukfall.getSjukskrivningsgrads());
        sjukfall.lan = extendedSjukfall.getLan();
        sjukfall.lakare = extendedSjukfall.getLakare();
        sjukfall.lastLakare = extendedSjukfall.getLastLakare();
        sjukfall.vardenhets = extendedSjukfall.getVardenhets();
        sjukfall.enhets = extendedSjukfall.getEnhets();
        sjukfall.lastEnhet = extendedSjukfall.getLastEnhet();
        sjukfall.lastVardEnhet = extendedSjukfall.getLastVardenhet();
        sjukfall.firstIntygId = extendedSjukfall.getFirstIntygId();
        sjukfall.intygCountIncludingBeforeCurrentPeriod = extendedSjukfall.getIntygCountIncludingBeforeCurrentPeriod();
        return sjukfall;
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
        return diagnos.getDiagnoskategori();
    }

    public int getDiagnoskod() {
        return diagnos.getDiagnoskod();
    }

    @SuppressWarnings("UnnecessaryDefaultInEnumSwitch")
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
                default:
                    throw new UnknownRangeTypeException("Unknown range type: " + rangeTypes);
            }
        }
        return result;
    }

    @SuppressWarnings("UnnecessaryDefaultInEnumSwitch")
    public List<Integer> getAllIcd10OfTypes(List<Icd10RangeType> icd10RangeTypes) {
        List<Integer> result = new ArrayList<>();
        for (Icd10RangeType icd10RangeType : icd10RangeTypes) {
            for (Diagnos diagnose : diagnoses) {
                switch (icd10RangeType) {
                    case KATEGORI:
                        result.add(diagnose.getDiagnoskategori());
                        break;
                    case AVSNITT:
                        result.add(diagnose.getDiagnosavsnitt());
                        break;
                    case KAPITEL:
                        result.add(diagnose.getDiagnoskapitel());
                        break;
                    case KOD:
                        result.add(diagnose.getDiagnoskod());
                        break;
                    default:
                        throw new UnknownRangeTypeException("Unknown icd range type: " + icd10RangeType);
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
        return diagnos.getDiagnoskapitel();
    }

    public Stream<Integer> getDiagnoskapitels() {
        return diagnoses.stream().map(d -> d.getDiagnoskapitel());
    }

    public int getSjukskrivningsgrad() {
        return sjukskrivningsgrad;
    }

    public List<Integer> getSjukskrivningsgrader() {
        return sjukskrivningsgrader;
    }

    public int getDiagnosavsnitt() {
        return diagnos.getDiagnosavsnitt();
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

    public Collection<HsaIdEnhet> getEnhets() {
        return enhets;
    }

    public Collection<HsaIdEnhet> getVardenhets() {
        return vardenhets;
    }

    public HsaIdEnhet getLastEnhet() {
        return lastEnhet;
    }

    public HsaIdEnhet getLastVardEnhet() {
        return lastVardEnhet;
    }

    public long getFirstIntygId() {
        return firstIntygId;
    }

    public int getIntygCountIncludingBeforeCurrentPeriod() {
        return intygCountIncludingBeforeCurrentPeriod;
    }

    public Diagnos getFirstDx() {
        return diagnoses.get(0);
    }

    public Diagnos getLastDx() {
        return diagnoses.get(diagnoses.size() - 1);
    }

    private static class UnknownRangeTypeException extends RuntimeException {

        UnknownRangeTypeException(String s) {
            super(s);
        }
    }
}
