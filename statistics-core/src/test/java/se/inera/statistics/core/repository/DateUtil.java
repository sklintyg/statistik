package se.inera.statistics.core.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import se.inera.statistics.model.entity.DateEntity;

public class DateUtil {
	public static void createDates(DateRepository dateRepository) {
		if (dateRepository.count() > 0) {
			return;
		}
		Locale locale = new Locale("sv");
		Calendar c = GregorianCalendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 2010);
		c.set(Calendar.DAY_OF_YEAR, 1);
		Calendar end = (Calendar) c.clone();
		end.add(Calendar.YEAR, 15);
		long id = 1;
		try {
			Constructor<DateEntity> dateEntityConstructor = DateEntity.class.getDeclaredConstructor();
			dateEntityConstructor.setAccessible(true);
			while (c.before(end)) {
				DateEntity dateEntity = dateEntityConstructor.newInstance();
				field("id").setLong(dateEntity, id);
				field("calendarDate").set(dateEntity, c.getTime());
				field("monthName").set(dateEntity, c.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
				int dom = c.get(Calendar.DAY_OF_MONTH);
				c.set(Calendar.DAY_OF_MONTH, 1);
				field("monthStart").set(dateEntity, c.getTime());
				c.roll(Calendar.DAY_OF_MONTH, -1);
				field("monthEnd").set(dateEntity, c.getTime());
				c.set(Calendar.DAY_OF_MONTH, dom);
				dateRepository.save(dateEntity);
				c.add(Calendar.DATE, 1);
				id ++;				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Field field(String fieldName) throws NoSuchFieldException {
		Field field = DateEntity.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}



}
