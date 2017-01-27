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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.Range;

public class SjukfallExtended {

    public static final int MAX_GAP = 5;

    private int start;
    private final int lan;
    private int end;
    private NavigableSet<Fact> facts = new TreeSet<>(START_DATUM_SORTER);
    private final int kon;
    private int alder;
    private List<Diagnos> diagnoses = new ArrayList<>();
    private Map<Range, Integer> sjukskrivningsgrad = new HashMap<>();
    private Set<Lakare> lakare = new HashSet<>();
    private Set<Integer> enhets = new HashSet<>();
    private SjukfallExtended extending;
    private List<Sjukskrivningsperiod> sjukskrivningsperiods = new ArrayList<>();
    private static final Comparator<Fact> START_DATUM_SORTER = new Comparator<Fact>() {
        @Override
        public int compare(Fact f1, Fact f2) {
            if (f1.getStartdatum() - f2.getStartdatum() == 0) {
                final int intygsIdCompare = Long.compare(f1.getLakarintyg(), f2.getLakarintyg());
                if (intygsIdCompare == 0) {
                    return f1.getSjukskrivningsgrad() - f2.getSjukskrivningsgrad();
                } else {
                    return intygsIdCompare;
                }
            } else {
                return f1.getStartdatum() - f2.getStartdatum();
            }
        }
    };

    public SjukfallExtended(Fact line) {
        start = line.getStartdatum();
        end = line.getSlutdatum();
        sjukskrivningsperiods.add(new Sjukskrivningsperiod(start, line.getSjukskrivningslangd()));
        facts.add(line);
        kon = line.getKon();
        alder = line.getAlder();
        diagnoses.add(new Diagnos(start, end, line.getDiagnoskapitel(), line.getDiagnosavsnitt(), line.getDiagnoskategori(), line.getDiagnoskod()));
        sjukskrivningsgrad.put(new Range(WidelineConverter.toDate(start), WidelineConverter.toDate(end)), line.getSjukskrivningsgrad());
        lan = line.getLan();
        this.lakare.add(getLakareFromFact(line));
        this.enhets.add(line.getEnhet());
        this.enkelt |= line.isEnkelt();
    }

    public SjukfallExtended(SjukfallExtended previous, Fact line) {
        this(line);
        start = Math.min(previous.getStart(), start);
        end = Math.max(previous.getEnd(), end);
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        facts.addAll(previous.facts);
        extending = previous;
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
        enhets.addAll(previous.getEnhets());
        sjukskrivningsgrad.putAll(previous.sjukskrivningsgrad);
        enkelt |= previous.isEnkelt();
    }

    SjukfallExtended(SjukfallExtended previous, SjukfallExtended sjukfall) {
        this(sjukfall);
        start = Math.min(this.start, previous.getStart());
        end = Math.max(this.end, previous.getEnd());
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        facts.addAll(previous.facts);
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
        enhets.addAll(previous.getEnhets());
        sjukskrivningsgrad.putAll(previous.sjukskrivningsgrad);
        enkelt |= previous.isEnkelt();
    }

    SjukfallExtended(SjukfallExtended sjukfall) {
        start = sjukfall.getStart();
        end = sjukfall.getEnd();
        sjukskrivningsperiods.addAll(sjukfall.sjukskrivningsperiods);
        facts.addAll(sjukfall.facts);
        kon = sjukfall.kon;
        alder = sjukfall.getAlder();
        diagnoses.addAll(sjukfall.diagnoses);
        sjukskrivningsgrad.putAll(sjukfall.sjukskrivningsgrad);
        lan = sjukfall.lan;
        lakare.addAll(sjukfall.getLakare());
        extending = sjukfall.extending;
        enhets.addAll(sjukfall.getEnhets());
        enkelt |= sjukfall.isEnkelt();
    }

    private Lakare getLakareFromFact(Fact line) {
        final int lakarid = line.getLakarid();
        final Kon lakarKon = Kon.byNumberRepresentation(line.getLakarkon());
        final int lakaralder = line.getLakaralder();
        final int[] lakarbefattnings = line.getLakarbefattnings();
        return new Lakare(lakarid, lakarKon, lakaralder, lakarbefattnings);
    }

    public int getKonInt() {
        return kon;
    }

    public Kon getKon() {
        return Kon.byNumberRepresentation(kon);
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @param line
     *            line
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public SjukfallExtended join(Fact line) {
        if (isExpired(line.getStartdatum())) {
            return newSjukfall(line);
        } else {
            return extendSjukfall(line);
        }
    }

    public SjukfallExtended newSjukfall(Fact line) {
        return new SjukfallExtended(line);
    }

    public SjukfallExtended extendSjukfall(Fact line) {
        return new SjukfallExtended(this, line);
    }

    public SjukfallExtended extendSjukfall(SjukfallExtended sjukfall) {
        return new SjukfallExtended(this, sjukfall);
    }

    public SjukfallExtended extendSjukfallWithNewStart(Fact intygForExtending) {
        final int startdatum = intygForExtending.getStartdatum();
        final int sjukskrivningslangd = intygForExtending.getSjukskrivningslangd();
        final SjukfallExtended sjukfall = new SjukfallExtended(this);
        sjukfall.start = startdatum;
        sjukfall.sjukskrivningsperiods.add(new Sjukskrivningsperiod(startdatum, sjukskrivningslangd));
        sjukfall.facts.add(intygForExtending);
        return sjukfall;
    }

    public SjukfallExtended extendSjukfallWithPeriods(SjukfallExtended sjukfallWithExtendingPeriods) {
        final SjukfallExtended sjukfall = new SjukfallExtended(this);
        for (Fact fact : sjukfallWithExtendingPeriods.facts) {
            sjukfall.sjukskrivningsperiods.add(new Sjukskrivningsperiod(fact.getStartdatum(), fact.getSjukskrivningslangd()));
        }
        return sjukfall;
    }

    private boolean isExpired(int datum) {
        return end + MAX_GAP + 1 < datum;
    }

    @Override
    public String toString() {
        return "Sjukfall{"
                + "start=" + WidelineConverter.toDate(start) + " (" + start + ")"
                + ", end=" + WidelineConverter.toDate(end) + " (" + end + ")"
                + ", realDays=" + getRealDays()
                + ", intygCount=" + getIntygCount()
                + '}';
    }

    public boolean in(int start, int end) {
        return !(this.end < start || this.start > end);
    }

    public int getDiagnoskategori() {
        return getLastDiagnosis().diagnoskategori;
    }

    public int getDiagnoskod() {
        return getLastDiagnosis().diagnoskod;
    }

    public int getAlder() {
        return alder;
    }

    public int getRealDays() {
        return getAllDates(sjukskrivningsperiods).size();
    }

    private HashSet<Integer> getAllDates(List<Sjukskrivningsperiod> periods) {
        final HashSet<Integer> allDates = new HashSet<>();
        for (Sjukskrivningsperiod sjukskrivningsperiod : periods) {
            allDates.addAll(sjukskrivningsperiod.getAllDatesInPeriod());
        }
        return allDates;
    }

    public int getIntygCount() {
        return facts.size();
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
        return getLastFact().getSjukskrivningsgrad();
    }

    public Collection<Integer> getSjukskrivningsgrads() {
        return sjukskrivningsgrad.values();
    }

    public int getDiagnosavsnitt() {
        return getLastDiagnosis().diagnosavsnitt;
    }

    Diagnos getLastDiagnosis() {
        return new Diagnos(getLastFact());
    }

    private Fact getLastFact() {
        return facts.last();
    }

    private Stream<Fact> getFirstIntygFacts() {
        Fact firstFact = getFirstFact();
        return facts.stream().filter(fact -> firstFact.getLakarintyg() == fact.getLakarintyg());
    }

    private Fact getFirstFact() {
        return facts.first();
    }

    public boolean isExtended() {
        return extending != null;
    }

    public String getLanskod() {
        return String.format("%1$02d", lan);
    }

    public int getLan() {
        return lan;
    }

    public Set<Lakare> getLakare() {
        return lakare;
    }

    public Lakare getLastLakare() {
        return getLakareFromFact(getLastFact());
    }

    public Set<Integer> getEnhets() {
        return enhets;
    }

    public int getLastEnhet() {
        return getLastFact().getEnhet();
    }

    public SjukfallExtended extendWithRealDaysWithinPeriod(SjukfallExtended previous) {
        final SjukfallExtended sjukfall = new SjukfallExtended(this);
        final Set<Integer> datesWithinPeriod = Sets.filter(previous.getAllDates(previous.sjukskrivningsperiods), new Predicate<Integer>() {
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

    public List<Diagnos> getAllDxs() {
        return diagnoses;
    }

    public boolean isEnkelt() {
        return this.enkelt;
    }

    public boolean containsAllIntygIn(SjukfallExtended sjukfallToCompare) {
        return getFactIds().containsAll(sjukfallToCompare.getFactIds());
    }

    private List<Long> getFactIds() {
        return facts.stream().map(Fact::getId).collect(Collectors.toList());
    }

    public long getFirstIntygId() {
        return getFirstFact().getLakarintyg();
    }

    public boolean containsAllIntygIn(SjukfallExtended sjukfallToCompare) {
        return getFactIds().containsAll(sjukfallToCompare.getFactIds());
    }

    private List<Long> getFactIds() {
        return facts.stream().map(Fact::getId).collect(Collectors.toList());
    }

    final class Diagnos {
        private final int diagnoskapitel;
        private final int diagnosavsnitt;
        private final int diagnoskategori;
        private final int diagnoskod;
        private final int startDatum;
        private final int slutDatum;

        private Diagnos(int startDatum, int slutDatum, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int diagnoskod) {
            this.startDatum = startDatum;
            this.slutDatum = slutDatum;
            this.diagnoskapitel = diagnoskapitel;
            this.diagnosavsnitt = diagnosavsnitt;
            this.diagnoskategori = diagnoskategori;
            this.diagnoskod = diagnoskod;
        }

        private Diagnos(Fact fact) {
            this(fact.getStartdatum(), fact.getSlutdatum(), fact.getDiagnoskapitel(), fact.getDiagnosavsnitt(), fact.getDiagnoskategori(),
                    fact.getDiagnoskod());
        }

        public int getDiagnoskapitel() {
            return diagnoskapitel;
        }

        public int getDiagnosavsnitt() {
            return diagnosavsnitt;
        }

        public int getDiagnoskategori() {
            return diagnoskategori;
        }

        public int getDiagnoskod() {
            return diagnoskod;
        }

        public int getStartDatum() {
            return startDatum;
        }

        public int getSlutDatum() {
            return slutDatum;
        }

    }

}
