/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.statistics.service.countypopulation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "CountyPopulation")
class CountyPopulationRow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String data;

    private Date date;

    CountyPopulationRow() {
    }

    CountyPopulationRow(String data, LocalDate date) {
        this.data = data;
        this.date = Date.valueOf(date);
    }

    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    String getData() {
        return data;
    }

    void setData(String data) {
        this.data = data;
    }

    LocalDate getDate() {
        return date.toLocalDate();
    }

    void setDate(LocalDate date) {
        this.date = Date.valueOf(date);
    }

}