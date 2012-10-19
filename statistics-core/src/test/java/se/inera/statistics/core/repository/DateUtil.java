package se.inera.statistics.core.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import se.inera.statistics.model.entity.DateEntity;

public class DateUtil {
	
	private static Locale LOCALE = new Locale("sv");

	public static void createDates(DateRepository dateRepository) {
		if (dateRepository.count() > 0) {
			return;
		}
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(parse("2010-01-01"));

		Calendar end = (Calendar) c.clone();
		end.add(Calendar.YEAR, 5);
		long id = 1;
		try {
			Constructor<DateEntity> dateEntityConstructor = DateEntity.class.getDeclaredConstructor();
			dateEntityConstructor.setAccessible(true);
			while (c.before(end)) {
				DateEntity dateEntity = dateEntityConstructor.newInstance();
				field("id").setLong(dateEntity, id);
				field("calendarDate").set(dateEntity, c.getTime());
				field("monthName").set(dateEntity, getMonthName(c));
				Calendar month = (Calendar) c.clone();
				month.set(Calendar.DAY_OF_MONTH, 1);
				field("monthStart").set(dateEntity, month.getTime());
				month.roll(Calendar.DAY_OF_MONTH, -1);
				field("monthEnd").set(dateEntity, month.getTime());

				dateRepository.save(dateEntity);

				nextDay(c);
				id ++;				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getMonthName(Calendar c) {
		return c.getDisplayName(Calendar.MONTH, Calendar.LONG, LOCALE);
	}

	private static void nextDay(Calendar c) {
		c.add(Calendar.DATE, 1);
	}
	
	private static Field field(String fieldName) throws NoSuchFieldException {
		Field field = DateEntity.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

	public static Date parse(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse string '" + dateString + "'", e);
		}
	}
}
