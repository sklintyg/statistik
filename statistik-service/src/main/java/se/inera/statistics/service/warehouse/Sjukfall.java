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

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.util.Icd10RangeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sjukfall {

    public static final int MAX_GAP = 5;

    private int start;
    private final int lan;
    private int end;
    private int realDays;
    private int intygCount;
    private final int kon;
    private int alder;
    private List<Diagnos> diagnoses = new ArrayList<>();
    private int sjukskrivningsgrad;
    private Set<Lakare> lakare = new HashSet<>();
    private Sjukfall extending;

    public Sjukfall(Fact line) {
        start = line.getStartdatum();
        end = line.getStartdatum() + line.getSjukskrivningslangd() - 1;
        realDays = line.getSjukskrivningslangd();
        intygCount++;
        kon = line.getKon();
        alder = line.getAlder();
        diagnoses.add(new Diagnos(line.getDiagnoskapitel(), line.getDiagnosavsnitt(), line.getDiagnoskategori()));
        sjukskrivningsgrad = line.getSjukskrivningsgrad();
        lan = line.getLan();
        final int lakarid = line.getLakarid();
        final Kon lakarKon = Kon.byNumberRepresentation(line.getLakarkon());
        final int lakaralder = line.getLakaralder();
        final int[] lakarbefattnings = line.getLakarbefattnings();
        this.lakare.add(new Lakare(lakarid, lakarKon, lakaralder, lakarbefattnings));
    }

    public Sjukfall(Sjukfall previous, Fact line) {
        this(line);
        start = previous.getStart();
        realDays += previous.getRealDays();
        intygCount += previous.getIntygCount();
        extending = previous;
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
    }

    Sjukfall(Sjukfall previous, Sjukfall sjukfall) {
        this(sjukfall);
        start = previous.getStart();
        realDays += previous.getRealDays() + (sjukfall.getStart() - previous.getEnd());
        intygCount += previous.getIntygCount();
        lakare.addAll(previous.getLakare());
        diagnoses.addAll(0, previous.diagnoses);
        alder = previous.alder > this.alder ? previous.alder : this.alder;
    }

    Sjukfall(Sjukfall sjukfall) {
        start = sjukfall.getStart();
        end = sjukfall.getEnd();
        realDays = sjukfall.getRealDays();
        intygCount = sjukfall.getIntygCount();
        kon = sjukfall.kon;
        alder = sjukfall.getAlder();
        diagnoses.addAll(sjukfall.diagnoses);
        sjukskrivningsgrad = sjukfall.getSjukskrivningsgrad();
        lan = sjukfall.lan;
        lakare.addAll(sjukfall.getLakare());
        extending = sjukfall.extending;
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
        sjukfall.realDays += sjukskrivningslangd;
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
                + ", realDays=" + realDays
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
        return realDays;
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
        return sjukskrivningsgrad;
    }

    public int getDiagnosavsnitt() {
        return getLastDiagnosis().diagnosavsnitt;
    }

    private Diagnos getLastDiagnosis() {
        return diagnoses.get(diagnoses.size() - 1);
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

    private final class Diagnos {
        private final int diagnoskapitel;
        private final int diagnosavsnitt;
        private final int diagnoskategori;

        private Diagnos(int diagnoskapitel, int diagnosavsnitt, int diagnoskategori) {
            this.diagnoskapitel = diagnoskapitel;
            this.diagnosavsnitt = diagnosavsnitt;
            this.diagnoskategori = diagnoskategori;
        }
    }

}
