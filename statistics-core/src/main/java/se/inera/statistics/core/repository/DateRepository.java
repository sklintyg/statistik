package se.inera.statistics.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import se.inera.statistics.model.entity.DateEntity;

public interface DateRepository extends JpaRepository<DateEntity, Long> {
	DateEntity findByMonthStart(Date calendarDate);
	DateEntity findByMonthEnd(Date calendarDate);
	DateEntity findByCalendarDate(Date calendarDate);
	
}