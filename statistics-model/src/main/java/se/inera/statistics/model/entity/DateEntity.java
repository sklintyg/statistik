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
	private Long id;

	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date calendarDate;
	
	@SuppressWarnings("unused")
	@Column(nullable=false)
	private int calendarYear;
	
	@SuppressWarnings("unused")
	@Column
	private int calendarMonth;
	
	@SuppressWarnings("unused")
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
	
	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	@Column
	@Temporal(TemporalType.DATE)
	private Date monthStart;
	
	@SuppressWarnings("unused")
	@Column
	@Temporal(TemporalType.DATE)
	private Date monthEnd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCalendarDate() {
		return calendarDate;
	}

	public void setCalendarDate(Date calendarDate) {
		this.calendarDate = calendarDate;
	}
}
