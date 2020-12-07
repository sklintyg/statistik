/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

import se.inera.statistics.integration.hsa.model.HsaIdEnhet;
import se.inera.statistics.integration.hsa.model.HsaIdLakare;
import se.inera.statistics.service.report.model.Kon;

public class FactBuilder {

    private long id = -1;
    private int lan = -1;
    private int kommun = -1;
    private int forsamling = -1;
    private HsaIdEnhet enhet = null;
    private HsaIdEnhet underenhet = null;
    private long lakarintyg = -1;
    private int patient = -1;
    private int startdatum = -1;
    private int slutdatum = -1;
    private int kon = -1;
    private int alder = -1;
    private int diagnoskapitel = -1;
    private int diagnosavsnitt = -1;
    private int diagnoskategori = -1;
    private int diagnoskod = -1;
    private int sjukskrivningsgrad = -1;
    private int lakarkon = -1;
    private int lakaralder = -1;
    private int[] lakarbefattnings = null;
    private HsaIdLakare lakarid = null;
    private Boolean enkelt = false;

    public static FactBuilder aFact() {
        return new FactBuilder();
    }

    @java.lang.SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1067"}) // I can't think of a better
    // way to write this
    public Fact build() {
        if (id == -1 || lan == -1 || kommun == -1 || forsamling == -1 || enhet == null || lakarintyg == -1 || patient == -1
            || startdatum == -1 || slutdatum == -1 || kon == -1 || alder == -1 || diagnoskapitel == -1 || diagnosavsnitt == -1
            || diagnoskategori == -1 || diagnoskod == -1 || sjukskrivningsgrad == -1 || lakarkon == -1
            || lakaralder == -1 || lakarbefattnings == null || lakarid == null || enkelt == null) {
            throw new UninitializedValuesException("unintialized values");
        }
        return new Fact(id, lan, kommun, forsamling, enhet, underenhet, lakarintyg, patient, startdatum, slutdatum, kon, alder, diagnoskapitel,
            diagnosavsnitt, diagnoskategori, diagnoskod, sjukskrivningsgrad, lakarkon,
            lakaralder, lakarbefattnings, lakarid);
    }

    public FactBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public FactBuilder withLan(int lan) {
        this.lan = lan;
        return this;
    }

    public FactBuilder withKommun(int kommun) {
        this.kommun = kommun;
        return this;
    }

    public FactBuilder withForsamling(int forsamling) {
        this.forsamling = forsamling;
        return this;
    }

    public FactBuilder withEnhet(int enhet) {
        this.enhet = new HsaIdEnhet(String.valueOf(enhet));
        return this;
    }

    public FactBuilder withUnderenhet(int underenhet) {
        this.underenhet = new HsaIdEnhet(String.valueOf(underenhet));
        return this;
    }

    public FactBuilder withLakarintyg(long lakarintyg) {
        this.lakarintyg = lakarintyg;
        return this;
    }

    public FactBuilder withPatient(int patient) {
        this.patient = patient;
        return this;
    }

    public FactBuilder withStartdatum(int startdatum) {
        this.startdatum = startdatum;
        return this;
    }

    public FactBuilder withSlutdatum(int slutdatum) {
        this.slutdatum = slutdatum;
        return this;
    }

    public FactBuilder withKon(Kon kon) {
        this.kon = kon == null ? Kon.UNKNOWN.getNumberRepresentation() : kon.getNumberRepresentation();
        return this;
    }

    public FactBuilder withAlder(int alder) {
        this.alder = alder;
        return this;
    }

    public FactBuilder withDiagnoskapitel(int diagnoskapitel) {
        this.diagnoskapitel = diagnoskapitel;
        return this;
    }

    public FactBuilder withDiagnosavsnitt(int diagnosavsnitt) {
        this.diagnosavsnitt = diagnosavsnitt;
        return this;
    }

    public FactBuilder withDiagnoskategori(int diagnoskategori) {
        this.diagnoskategori = diagnoskategori;
        return this;
    }

    public FactBuilder withDiagnoskod(int diagnoskod) {
        this.diagnoskod = diagnoskod;
        return this;
    }

    public FactBuilder withSjukskrivningsgrad(int sjukskrivningsgrad) {
        this.sjukskrivningsgrad = sjukskrivningsgrad;
        return this;
    }

    public FactBuilder withLakarkon(Kon lakarkon) {
        this.lakarkon = lakarkon == null ? Kon.UNKNOWN.getNumberRepresentation() : lakarkon.getNumberRepresentation();
        return this;
    }

    public FactBuilder withLakaralder(int lakaralder) {
        this.lakaralder = lakaralder;
        return this;
    }

    public FactBuilder withLakarbefattning(int[] lakarbefattnings) {
        this.lakarbefattnings = lakarbefattnings;
        return this;
    }

    public FactBuilder withLakarid(int lakarid) {
        this.lakarid = new HsaIdLakare(String.valueOf(lakarid));
        return this;
    }

    public FactBuilder withLakarid(HsaIdLakare lakarid) {
        this.lakarid = lakarid;
        return this;
    }

    public static Fact newFact(long id, int lan, int kommun, int forsamling, int enhet, int underenhet, long lakarintyg, long patient, int startdatum,
        int slutdatum,
        int kon, int alder, int diagnoskapitel, int diagnosavsnitt, int diagnoskategori, int diagnoskod, int sjukskrivningsgrad,
        int lakarkon, int lakaralder, int[] lakarbefattnings, int lakarid) {
        return new Fact(id, lan, kommun, forsamling, new HsaIdEnhet(String.valueOf(enhet)), new HsaIdEnhet(String.valueOf(underenhet)),
                lakarintyg, patient, startdatum, slutdatum, kon, alder, diagnoskapitel, diagnosavsnitt, diagnoskategori, diagnoskod,
                sjukskrivningsgrad, lakarkon, lakaralder, lakarbefattnings, new HsaIdLakare(String.valueOf(lakarid)));
    }

    private static class UninitializedValuesException extends RuntimeException {

        private static final long serialVersionUID = 8983497734628986363L;

        UninitializedValuesException(String s) {
            super(s);
        }
    }
}