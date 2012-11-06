/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
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
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"calendarDate"}))
public class DateEntity {
	DateEntity(){
	}
	
	@Id
	private long id;

	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date calendarDate;
	
	@SuppressWarnings("unused")
	@Column(nullable=false)
	private int calendarYear;
	
	@SuppressWarnings("unused")
	@Column
	private int calendarMonth;
	
//	@SuppressWarnings("unused")
	@Column
	private String monthName;
	
	@SuppressWarnings("unused")
	@Column(nullable=false)
	private int calendarDay;
	
	@SuppressWarnings("unused")
	@Column
	private int dayOfYear;
	
	@SuppressWarnings("unused")
	@Column
	private String weekDay;
	
	@SuppressWarnings("unused")
	@Column
	private int calendarWeek;
	
	@SuppressWarnings("unused")
	@Column
	private String formattedDate;
	
	@SuppressWarnings("unused")
	@Column
	private String quartal;
	
	@SuppressWarnings("unused")
	@Column
	private String yearQuartal;
	
//	@SuppressWarnings("unused")
	@Column
	private String yearMonth;
	
	@SuppressWarnings("unused")
	@Column
	private String yearCalendarWeek;
	
	@SuppressWarnings("unused")
	@Column
	private String weekend;
	
	@SuppressWarnings("unused")
	@Column
	@Temporal(TemporalType.DATE)
	private Date currentWeekStart;

	@SuppressWarnings("unused")
	@Column
	@Temporal(TemporalType.DATE)
	private Date currentWeekEnd;

//	@SuppressWarnings("unused")
	@Column
	@Temporal(TemporalType.DATE)
	private Date monthStart;
	
//	@SuppressWarnings("unused")
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
