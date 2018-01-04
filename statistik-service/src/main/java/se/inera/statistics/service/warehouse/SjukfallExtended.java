/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import se.inera.statistics.service.report.model.Kon;

public class SjukfallExtended {

    public static final int MAX_GAP = 5;

    private int start;
    private int end;
    private NavigableSet<Fact> facts = new TreeSet<>(START_DATUM_SORTER);
    private List<Long> factIds;
    private SjukfallExtended extending;
    private List<Sjukskrivningsperiod> sjukskrivningsperiods = new ArrayList<>();
    private static final Comparator<Fact> START_DATUM_SORTER = (f1, f2) -> {
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
    };

    public SjukfallExtended(Fact line) {
        start = line.getStartdatum();
        end = line.getSlutdatum();
        sjukskrivningsperiods.add(new Sjukskrivningsperiod(start, line.getSjukskrivningslangd()));
        facts.add(line);
    }

    public SjukfallExtended(SjukfallExtended previous, Fact line) {
        this(line);
        start = Math.min(previous.getStart(), start);
        end = Math.max(previous.getEnd(), end);
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        facts.addAll(previous.facts);
        extending = previous;
    }

    SjukfallExtended(SjukfallExtended previous, SjukfallExtended sjukfall) {
        this(sjukfall);
        start = Math.min(this.start, previous.getStart());
        end = Math.max(this.end, previous.getEnd());
        sjukskrivningsperiods.addAll(previous.sjukskrivningsperiods);
        facts.addAll(previous.facts);
    }

    private SjukfallExtended(SjukfallExtended sjukfall) {
        start = sjukfall.getStart();
        end = sjukfall.getEnd();
        sjukskrivningsperiods.addAll(sjukfall.sjukskrivningsperiods);
        facts.addAll(sjukfall.facts);
        extending = sjukfall.extending;
    }

    private Lakare getLakareFromFact(Fact line) {
        final int lakarid = line.getLakarid();
        final Kon lakarKon = Kon.byNumberRepresentation(line.getLakarkon());
        final int lakaralder = line.getLakaralder();
        final int[] lakarbefattnings = line.getLakarbefattnings();
        return new Lakare(lakarid, lakarKon, lakaralder, lakarbefattnings);
    }

    int getKonInt() {
        return facts.last().getKon();
    }

    /**
     * Checks if the given Fact is part of this Sjukfall.
     * If so, this sjukfall is updated and return,
     * otherwise a new sjukfall is created.
     *
     * @return join will either return the same, possibly modified (i.e. this), Sjukfall-object, or a new object
     */
    public SjukfallExtended join(Fact line) {
        if (isExpired(line.getStartdatum())) {
            return newSjukfall(line);
        } else {
            return extendSjukfall(line);
        }
    }

    private SjukfallExtended newSjukfall(Fact line) {
        return new SjukfallExtended(line);
    }

    SjukfallExtended extendSjukfall(Fact line) {
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
        return getLastDiagnosis().getDiagnoskategori();
    }

    public int getDiagnoskod() {
        return getLastDiagnosis().getDiagnoskod();
    }

    public int getAlder() {
        return facts.stream().map(Fact::getAlder).max(Comparator.comparingInt(v -> v)).orElse(0);
    }

    public int getRealDays() {
        return Sjukskrivningsperiod.getLengthOfJoinedPeriods(sjukskrivningsperiods);
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
        return getLastDiagnosis().getDiagnoskapitel();
    }

    public int getSjukskrivningsgrad() {
        return getLastFact().getSjukskrivningsgrad();
    }

    Collection<Integer> getSjukskrivningsgrads() {
        return facts.stream().map(Fact::getSjukskrivningsgrad).collect(Collectors.toList());
    }

    public int getDiagnosavsnitt() {
        return getLastDiagnosis().getDiagnosavsnitt();
    }

    Diagnos getLastDiagnosis() {
        return new Diagnos(getLastFact());
    }

    private Fact getLastFact() {
        return facts.last();
    }

    private Fact getFirstFact() {
        return facts.first();
    }

    public boolean isExtended() {
        return extending != null;
    }

    String getLanskod() {
        return String.format("%1$02d", getLan());
    }

    public int getLan() {
        return facts.last().getLan();
    }

    public Set<Lakare> getLakare() {
        return facts.stream().sorted(START_DATUM_SORTER.reversed()).map(this::getLakareFromFact).collect(Collectors.toSet());
    }

    Lakare getLastLakare() {
        return getLakareFromFact(getLastFact());
    }

    public Set<Integer> getEnhets() {
        return facts.stream().map(Fact::getEnhet).collect(Collectors.toSet());
    }

    int getLastEnhet() {
        return getLastFact().getEnhet();
    }

    public SjukfallExtended extendWithRealDaysWithinPeriod(SjukfallExtended previous) {
        final SjukfallExtended sjukfall = new SjukfallExtended(this);
        for (Sjukskrivningsperiod sjukskrivningsperiod : previous.sjukskrivningsperiods) {
            final int prevStart = sjukskrivningsperiod.getStart();
            final int prevEnd = prevStart + sjukskrivningsperiod.getLength();
            final boolean startIsInPeriod = prevStart > this.start && prevStart < this.end;
            final boolean endIsInPeriod = prevEnd < this.end && prevEnd > this.start;
            if (startIsInPeriod && endIsInPeriod) {
                sjukfall.sjukskrivningsperiods.add(sjukskrivningsperiod);
            } else if (startIsInPeriod || endIsInPeriod) {
                final int startInPeriod = Math.max(prevStart, this.start);
                final int endInPeriod = Math.min(prevEnd, this.end);
                final int newPeriodLength = endInPeriod - startInPeriod;
                sjukfall.sjukskrivningsperiods.add(new Sjukskrivningsperiod(startInPeriod, newPeriodLength));
            }
        }
        return sjukfall;
    }

    List<Diagnos> getAllDxs() {
        return facts.stream().map(Diagnos::new).collect(Collectors.toList());
    }

    long getFirstIntygId() {
        return getFirstFact().getLakarintyg();
    }

    public boolean containsAllIntygIn(SjukfallExtended sjukfallToCompare) {
        return getFactIds().containsAll(sjukfallToCompare.getFactIds());
    }

    private List<Long> getFactIds() {
        if (factIds == null) {
            factIds = facts.stream().map(Fact::getId).collect(Collectors.toList());
        }
        return factIds;
    }

}
