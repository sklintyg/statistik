/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.statistics

import se.inera.statistics.spec.DagensDatum
import se.inera.statistics.spec.EnbartFoljandeIntygFinns
import se.inera.statistics.spec.TroskelVarde
import se.inera.statistics.web.reports.ReportsUtil


class InsertIntygHelper {

    public static void main(String... args) {
        new DagensDatum("2016-11-01"); //Will set the date "fitnesse-style"
        new TroskelVarde(5); //Will set the troskelvarde "fitnesse-style"

        ReportsUtil util = new ReportsUtil();

        util.clearRegionFileUploads();
        util.insertRegion(ReportsUtil.VARDGIVARE3)

        ArrayList<IntygData> intygs = getIntygs()

        def fitnessIntygInjector = new EnbartFoljandeIntygFinns();
        fitnessIntygInjector.beginTable();
        for (IntygData intygData : intygs ) {
            fitnessIntygInjector.reset();
            fitnessIntygInjector.setPersonnr(intygData.pnr);
            fitnessIntygInjector.setStart(intygData.start);
            fitnessIntygInjector.setSlut(intygData.end);
            fitnessIntygInjector.setEnhet(intygData.enhet);
            fitnessIntygInjector.setVardgivare(intygData.vg);
            fitnessIntygInjector.setDiagnoskod(intygData.dx);
            fitnessIntygInjector.setArbetsförmåga(intygData.arbetsformaga);
            fitnessIntygInjector.execute();
        }
        fitnessIntygInjector.endTable();
    }

    private static ArrayList<IntygData> getIntygs() {
        def intygs = new ArrayList<IntygData>();
        // 9 lika
        intygs.add(new IntygData("19790407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1291", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1293", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1294", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1295", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1296", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1297", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1298", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1299", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));

        //Olika diagnoser
        intygs.add(new IntygData("19790407-2291", "2016-09-01", "2016-11-30", "enhet1", "vg1", "B01", 0));
        intygs.add(new IntygData("19790407-2292", "2016-09-01", "2016-11-30", "enhet1", "vg1", "B02", 0));
        intygs.add(new IntygData("19790407-2293", "2016-09-01", "2016-11-30", "enhet1", "vg1", "B03", 0));
        intygs.add(new IntygData("19790407-2294", "2016-09-01", "2016-11-30", "enhet1", "vg1", "B25", 0));
        intygs.add(new IntygData("19790407-2295", "2016-09-01", "2016-11-30", "enhet1", "vg1", "B26", 0));
        intygs.add(new IntygData("19790407-2296", "2016-09-01", "2016-11-30", "enhet1", "vg1", "D50", 0));
        intygs.add(new IntygData("19790407-2297", "2016-09-01", "2016-11-30", "enhet1", "vg1", "D51", 0));
        intygs.add(new IntygData("19790407-2298", "2016-09-01", "2016-11-30", "enhet1", "vg1", "F010", 0));
        intygs.add(new IntygData("19790407-2299", "2016-09-01", "2016-11-30", "enhet1", "vg1", "F020", 0));

        // Olika längder
        intygs.add(new IntygData("19790407-3290", "2016-09-01", "2016-09-10", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3291", "2016-09-01", "2016-09-20", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3292", "2016-09-01", "2016-10-10", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3293", "2016-09-01", "2016-11-10", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3294", "2016-07-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3295", "2016-01-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3296", "2015-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3297", "2016-09-01", "2016-09-02", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-3299", "2016-09-01", "2016-09-30", "enhet1", "vg1", "A01", 0));

        // Troskelvarde (A30 over och A31 under)
        intygs.add(new IntygData("19790407-4290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A30", 0));
        intygs.add(new IntygData("19790407-4291", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A30", 0));
        intygs.add(new IntygData("19790407-4292", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A30", 0));
        intygs.add(new IntygData("19790407-4293", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A30", 0));
        intygs.add(new IntygData("19790407-4294", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A30", 0));
        intygs.add(new IntygData("19790407-4295", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A31", 0));
        intygs.add(new IntygData("19790407-4297", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A31", 0));
        intygs.add(new IntygData("19790407-4298", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A31", 0));
        intygs.add(new IntygData("19790407-4299", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A31", 0));

        // Olika enheter och vardgivare
        intygs.add(new IntygData("19790407-5290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-5291", "2016-09-01", "2016-11-30", "enhet2", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-5292", "2016-09-01", "2016-11-30", "enhet2", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-5294", "2016-09-01", "2016-11-30", "enhet3", "vg3", "A01", 0));
        intygs.add(new IntygData("19790407-5295", "2016-09-01", "2016-11-30", "enhet3", "vg3", "A01", 0));
        intygs.add(new IntygData("19790407-5296", "2016-09-01", "2016-11-30", "enhet3", "vg3", "A01", 0));
        intygs.add(new IntygData("19790407-5297", "2016-09-01", "2016-11-30", "enhet4", "vg3", "A01", 0));
        intygs.add(new IntygData("19790407-5298", "2016-09-01", "2016-11-30", "enhet4", "vg3", "A01", 0));
        intygs.add(new IntygData("19790407-5299", "2016-09-01", "2016-11-30", "enhet4", "vg3", "A01", 0));

        // Olika kon
        intygs.add(new IntygData("19790407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1291", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1293", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1294", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1285", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1286", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1287", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1288", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19790407-1289", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));

        // Olika sjukskrivningsgrad
        intygs.add(new IntygData("19790407-6290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 25));
        intygs.add(new IntygData("19790407-6292", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 25));
        intygs.add(new IntygData("19790407-6293", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 25));
        intygs.add(new IntygData("19790407-6294", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 25));
        intygs.add(new IntygData("19790407-6295", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 50));
        intygs.add(new IntygData("19790407-6296", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 50));
        intygs.add(new IntygData("19790407-6297", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 75));
        intygs.add(new IntygData("19790407-6298", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 75));
        intygs.add(new IntygData("19790407-6299", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 75));

        // Olika alder
        intygs.add(new IntygData("20100407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("20000407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19900407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19800407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19700407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19600407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19500407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19400407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs.add(new IntygData("19000407-1290", "2016-09-01", "2016-11-30", "enhet1", "vg1", "A01", 0));
        intygs
    }

    private static class IntygData {
        def pnr;
        def start;
        def end;
        def enhet;
        def vg;
        def dx;
        def arbetsformaga;

        IntygData(pnr, start, end, enhet, vg, dx, arbetsformaga) {
            this.pnr = pnr
            this.start = start
            this.end = end
            this.enhet = enhet
            this.vg = vg
            this.dx = dx
            this.arbetsformaga = arbetsformaga
        }
    }


}
