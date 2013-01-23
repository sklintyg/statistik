/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This file is part of Inera Statistics (http://code.google.com/p/inera-statistics).
 *
 * Inera Statistics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Inera Statistics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "calendarDate" }))
public class DateEntity {
    DateEntity() {
    }

    @Id
    private long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date calendarDate;

    @Column(nullable = false)
    private int calendarYear;

    @Column
    private int calendarMonth;

    // @SuppressWarnings("unused")
    @Column
    private String monthName;

    @Column(nullable = false)
    private int calendarDay;

    @Column
    private int dayOfYear;

    @Column
    private String weekDay;

    @Column
    private int calendarWeek;

    @Column
    private String formattedDate;

    @Column
    private String quartal;

    @Column
    private String yearQuartal;

    @Column
    private String yearMonth;

    @Column
    private String yearCalendarWeek;

    @Column
    private String weekend;

    @Column
    @Temporal(TemporalType.DATE)
    private Date currentWeekStart;

    @Column
    @Temporal(TemporalType.DATE)
    private Date currentWeekEnd;

    @Column
    @Temporal(TemporalType.DATE)
    private Date monthStart;

    @Column
    @Temporal(TemporalType.DATE)
    private Date monthEnd;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;

    }

    public Date getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Date calendarDate) {
        this.calendarDate = calendarDate;
    }

    public Date getMonthStart() {
        return monthStart;
    }

    public Date getMonthEnd() {
        return monthEnd;
    }

    public String getMonthName() {
        return monthName;
    }

    public String getYearMonth() {
        return yearMonth;
    }
}
