/**
 * Copyright (C) 2014 Inera AB (http://www.inera.se)
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

class PersonifiedSjukfall {

    private int patient;
    private Sjukfall sjukfall;

    PersonifiedSjukfall(Fact line) {
        sjukfall = new Sjukfall(line);
        this.patient = line.getPatient();
    }

    PersonifiedSjukfall(Sjukfall previous, int patient) {
        sjukfall = previous;
        this.patient = patient;
    }

    int getPatient() {
        return patient;
    }

    Sjukfall getSjukfall() {
        return sjukfall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PersonifiedSjukfall that = (PersonifiedSjukfall) o;

        if (patient != that.patient) {
            return false;
        }
        if (sjukfall != null ? !sjukfall.equals(that.sjukfall) : that.sjukfall != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = patient;
        final int magicNumberGeneratedByIdea = 31;
        result = magicNumberGeneratedByIdea * result + (sjukfall != null ? sjukfall.hashCode() : 0);
        return result;
    }

}
