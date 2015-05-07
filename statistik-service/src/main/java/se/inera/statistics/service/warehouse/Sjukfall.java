/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;
import se.inera.statistics.service.report.util.Icd10RangeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    private int start;
    private final int lan;
    private int end;
    private int intygCount;
    private final int kon;
    private int alder;
    private List<Diagnos> diagnoses = new ArrayList<>();
    private Map<Range, Integer> sjukskrivningsgrad = new HashMap<>();
    private Set<Lakare> lakare = new HashSet<>();
    private Set<Integer> enhets = new HashSet<>();
    private Sjukfall extending;
    private List<Sjukskrivningsperiod> sjukskrivningsperiods = new ArrayList<>();

    public Sjukfall(Fact line) {
        start = line.getStartdatum();
        end = line.getStartdatum() + line.getSjukskrivningslangd() - 1;
        sjukskrivningsperiods.add(new Sjukskrivningsperiod(start, line.getSjukskrivningslangd()));
        intygCount++;
        kon = line.getKon();
        alder = line.getAlder();
        diagnoses.add(new Diagnos(start, end, line.getDiagnoskapitel(), line.getDiagnosavsnitt(), line.getDiagnoskategori()));
        sjukskrivningsgrad.put(new Range(WidelineConverter.toDate(start), WidelineConverter.toDate(end)), line.getSjukskrivningsgrad());
        lan = line.getLan();
        final int lakarid = line.getLakarid();
        final Kon lakarKon = Kon.byNumberRepresentation(line.getLakarkon());
        final int lakaralder = line.getLakaralder();
        final int[] lakarbefattnings = line.getLakarbefattnings();
        this.lakare.add(new Lakare(lakarid, lakarKon, lakaralder, lakarbefattnings));
        this.enhets.add(line.getEnhet());
    }

    public Sjukfall(Sjukfall previous, Fact line) {
        this(line);
        start = Math.min(previous.getStart(), start);
        end = Math.max(previous.getEnd(), end);
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        intygCount += previous.getIntygCount();
        extending = previous;
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
        enhets.addAll(previous.getEnhets());
        sjukskrivningsgrad.putAll(previous.sjukskrivningsgrad);
    }

    Sjukfall(Sjukfall previous, Sjukfall sjukfall) {
        this(sjukfall);
        start = previous.getStart();
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        intygCount += previous.getIntygCount();
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
        enhets.addAll(previous.getEnhets());
        sjukskrivningsgrad.putAll(previous.sjukskrivningsgrad);
    }

    Sjukfall(Sjukfall sjukfall) {
        start = sjukfall.getStart();
        end = sjukfall.getEnd();
        sjukskrivningsperiods.addAll(sjukfall.sjukskrivningsperiods);
        intygCount = sjukfall.getIntygCount();
        kon = sjukfall.kon;
        alder = sjukfall.getAlder();
        diagnoses.addAll(sjukfall.diagnoses);
        sjukskrivningsgrad.putAll(sjukfall.sjukskrivningsgrad);
        lan = sjukfall.lan;
        lakare.addAll(sjukfall.getLakare());
        extending = sjukfall.extending;
        enhets.addAll(sjukfall.getEnhets());
    }

    public Kon getKon() {
        return Kon.byNumberRepresentation(kon);
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public Sjukfall join(Fact line) {
        if (isExpired(line.getStartdatum())) {
            return newSjukfall(line);
        } else {
            return extendSjukfall(line);
        }
    }

    public Sjukfall newSjukfall(Fact line) {
        return new Sjukfall(line);
    }

    public Sjukfall extendSjukfall(Fact line) {
        return new Sjukfall(this, line);
    }

    public Sjukfall extendSjukfall(Sjukfall sjukfall) {
        return new Sjukfall(this, sjukfall);
    }

    public Sjukfall extendSjukfallWithNewStart(int start, int sjukskrivningslangd) {
        final Sjukfall sjukfall = new Sjukfall(this);
        sjukfall.start = start;
        sjukfall.sjukskrivningsperiods.add(new Sjukskrivningsperiod(start, sjukskrivningslangd));
        return sjukfall;
    }

    private boolean isExpired(int datum) {
        return end + MAX_GAP + 1 < datum;
    }

    @Override
    public String toString() {
        return "Sjukfall{"
                + "start=" + start
                + ", end=" + end
                + ", realDays=" + getRealDays()
                + ", intygCount=" + intygCount
                + '}';
    }

    public boolean in(int start, int end) {
        return !(this.end < start || this.start > end);
    }

    public int getDiagnoskategori() {
        return getLastDiagnosis().diagnoskategori;
    }

    public int getIcd10CodeForType(Icd10RangeType rangeType) {
        switch (rangeType) {
            case KAPITEL: return getDiagnoskapitel();
            case AVSNITT: return getDiagnosavsnitt();
            case KATEGORI: return getDiagnoskategori();
            default: throw new RuntimeException("Unknown range type: " + rangeType);
        }
    }

    public List<Integer> getAllIcd10OfType(Icd10RangeType icd10RangeType) {
        List<Integer> result = new ArrayList<>();
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
                default: throw new RuntimeException("Unknown icd range type: " + icd10RangeType);
            }
        }
        return result;
    }

    public int getAlder() {
        return alder;
    }

    public int getRealDays() {
        return getAllDates().size();
    }

    private HashSet<Integer> getAllDates() {
        final HashSet<Integer> allDates = new HashSet<>();
        for (Sjukskrivningsperiod sjukskrivningsperiod : sjukskrivningsperiods) {
            allDates.addAll(sjukskrivningsperiod.getAllDatesInPeriod());
        }
        return allDates;
    }

    public int getIntygCount() {
        return intygCount;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDiagnoskapitel() {
        return getLastDiagnosis().diagnoskapitel;
    }

    public int getSjukskrivningsgrad() {
        Map.Entry<Range, Integer> currentFound = null;
        for (Map.Entry<Range, Integer> entry : sjukskrivningsgrad.entrySet()) {
            if (currentFound == null || entry.getKey().getFrom().isAfter(currentFound.getKey().getFrom())) {
                currentFound = entry;
            }
        }
        return currentFound.getValue();
    }

    public int getDiagnosavsnitt() {
        return getLastDiagnosis().diagnosavsnitt;
    }

    private Diagnos getLastDiagnosis() {
        Diagnos currentFoundDiagnos = null;
        for (Diagnos diagnose : diagnoses) {
            if (currentFoundDiagnos == null || diagnose.startDatum > currentFoundDiagnos.startDatum) {
                currentFoundDiagnos = diagnose;
            }
        }
        return currentFoundDiagnos;
    }

    public boolean isExtended() {
        return extending != null;
    }

    public String getLanskod() {
        return String.format("%1$02d", lan);
    }

    public Set<Lakare> getLakare() {
        return lakare;
    }

    public Set<Integer> getEnhets() {
        return enhets;
    }

    public Sjukfall extendWithRealDaysWithinPeriod(Sjukfall previous) {
        final Sjukfall sjukfall = new Sjukfall(this);
        final Set<Integer> datesWithinPeriod = Sets.filter(previous.getAllDates(), new Predicate<Integer>() {
            @Override
            public boolean apply(Integer date) {
                return date >= start && date <= end;
            }
        });
        List<Sjukskrivningsperiod> newSjukskrivningsperiods = toSjukskrivningsperiods(datesWithinPeriod);
        sjukfall.sjukskrivningsperiods.addAll(newSjukskrivningsperiods);
        return sjukfall;
    }

    private List<Sjukskrivningsperiod> toSjukskrivningsperiods(Set<Integer> dates) {
        if (dates.isEmpty()) {
            return Collections.emptyList();
        }
        final ArrayList<Sjukskrivningsperiod> result = new ArrayList<>();
        final SortedSet<Integer> sortedDates = new TreeSet<>(dates);
        final List<Integer> currentPeriodDates = new ArrayList<>(sortedDates.size());
        for (Integer date : sortedDates) {
            if (!currentPeriodDates.isEmpty() && date > currentPeriodDates.get(currentPeriodDates.size() - 1) + 1) {
                result.add(new Sjukskrivningsperiod(currentPeriodDates.get(0), currentPeriodDates.size()));
                currentPeriodDates.clear();
            }
            currentPeriodDates.add(date);
        }
        if (!currentPeriodDates.isEmpty()) {
            result.add(new Sjukskrivningsperiod(currentPeriodDates.get(0), currentPeriodDates.size()));
        }
        return result;
    }

    private final class Diagnos {
        private final int diagnoskapitel;
        private final int diagnosavsnitt;
        private final int diagnoskategori;
        private final int startDatum;
        private final int slutDatum;

        private Diagnos(int startDatum, int slutDatum, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori) {
            this.startDatum = startDatum;
            this.slutDatum = slutDatum;
            this.diagnoskapitel = diagnoskapitel;
            this.diagnosavsnitt = diagnosavsnitt;
            this.diagnoskategori = diagnoskategori;
        }
    }

}
