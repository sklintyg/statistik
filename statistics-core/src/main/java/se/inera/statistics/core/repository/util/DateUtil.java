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
package se.inera.statistics.core.repository.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.management.RuntimeErrorException;

import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.model.entity.DateEntity;

public class DateUtil {
	
	private static final Locale LOCALE = new Locale("sv");

	public static void createDates(DateRepository dateRepository, String from, String to) {
		if (dateRepository.count() > 0) {
			return;
		}
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(parse(from));

		Calendar end = (Calendar) c.clone();
		end.setTime(parse(to));
		long id = 1;
		try {
			Constructor<DateEntity> dateEntityConstructor = DateEntity.class.getDeclaredConstructor();
			dateEntityConstructor.setAccessible(true);
			while (c.before(end)) {
				DateEntity dateEntity = dateEntityConstructor.newInstance();
				set("id", dateEntity, id);
				set("calendarDate", dateEntity, c.getTime());
				set("monthName", dateEntity, getMonthName(c));
				Calendar month = (Calendar) c.clone();
				month.set(Calendar.DAY_OF_MONTH, 1);
				set("monthStart", dateEntity, month.getTime());
				month.roll(Calendar.DAY_OF_MONTH, -1);
				set("monthEnd", dateEntity, month.getTime());

				dateRepository.save(dateEntity);

				nextDay(c);
				id ++;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	
    private static void set(String fieldName, Object object, Object value) throws RuntimeException, IllegalAccessException, NoSuchFieldException {
        field(fieldName).set(object, value);
    }

    private static void set(String fieldName, Object object, long value) throws RuntimeException, IllegalAccessException, NoSuchFieldException {
        field(fieldName).setLong(object, value);
    }

	public static Date parse(String dateString) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse string '" + dateString + "'", e);
		}
	}
}
