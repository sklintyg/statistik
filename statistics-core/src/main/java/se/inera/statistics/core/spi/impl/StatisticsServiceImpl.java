package se.inera.statistics.core.spi.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.inera.commons.support.ServiceMessageType;
import se.inera.commons.support.ServiceResult;
import se.inera.commons.support.impl.DefaultServiceMessage;
import se.inera.commons.support.impl.ServiceResultImpl;
import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.api.RowResult;
import se.inera.statistics.core.api.StatisticsResult;
import se.inera.statistics.core.repository.CareUnitRepository;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.spi.StatisticsService;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DateEntity;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

	private static final String TIME_TEXT_FORMAT = "MMM yy";

	private static final Locale LOCALE = new Locale("sv");

	private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);
	
	@Autowired
	private MedicalCertificateRepository certificateRepository;
	
	@Autowired
	private DateRepository dateRepository;	
	
	@Autowired
	private CareUnitRepository careUnitRepository;
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByDuration(final MedicalCertificateDto search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
			
			final DateEntity startDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getStartDate()));
			final DateEntity endDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getEndDate()));
			final long start = this.dateRepository.findByCalendarDate(startDate.getMonthStart()).getId();
			final long end = this.dateRepository.findByCalendarDate(endDate.getMonthEnd()).getId();

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByDuration(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ServiceResultImpl.newSuccessfulResult(
					result,
					Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS))
					);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByMonth(final MedicalCertificateDto search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
			
			final DateEntity startDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getStartDate()));
			final DateEntity start = this.dateRepository.findByCalendarDate(startDate.getMonthStart());

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByMonth(start, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ServiceResultImpl.newSuccessfulResult(
					result,
					Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS))
					);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByCareUnit(final MedicalCertificateDto search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
			
			final DateEntity startDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getStartDate()));
			final DateEntity endDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getEndDate()));
			final long start = this.dateRepository.findByCalendarDate(startDate.getMonthStart()).getId();
			final long end = this.dateRepository.findByCalendarDate(endDate.getMonthEnd()).getId();

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByCareUnit(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ServiceResultImpl.newSuccessfulResult(
					result,
					Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS))
					);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadBySearch(MedicalCertificateDto search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
			
			final DateEntity startDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getStartDate()));
			final DateEntity endDate = this.dateRepository.findByCalendarDate(sdf.parse(search.getEndDate()));

			final long start = this.dateRepository.findByCalendarDate(startDate.getMonthStart()).getId();
			final long end = this.dateRepository.findByCalendarDate(endDate.getMonthEnd()).getId();

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByAge(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ServiceResultImpl.newSuccessfulResult(
					result,
					Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS))
					);

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	private List<RowResult> getRowResultsByDuration(long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		rowResults.add(getRowResultByDuration(0, 14, start, end, basedOnExamination, basedOnTelephoneContact));
		rowResults.add(getRowResultByDuration(15, 30, start, end, basedOnExamination, basedOnTelephoneContact));
		rowResults.add(getRowResultByDuration(31, 90, start, end, basedOnExamination, basedOnTelephoneContact));
		rowResults.add(getRowResultByDuration(91, 360, start, end, basedOnExamination, basedOnTelephoneContact));
//		rowResults.add(getRowResultByDuration(361, 1500, start, end, basedOnExamination, basedOnTelephoneContact));
		
		return rowResults;
	}
	
	private RowResult getRowResultByDuration(long minDuration, long maxDuration, long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final int count_male = (int)this.certificateRepository.findCountByDuration(minDuration, maxDuration, "Male", start, end, basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountByDuration(minDuration, maxDuration, "Female", start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult("" + minDuration + "-" + maxDuration, count_male, count_female);

		if (0 == minDuration){
			row.setxValue("<" + maxDuration);
		}
		
		return row;
	}
	
	private List<RowResult> getRowResultsByAge(long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		rowResults.add(getRowResultByAge(0, 19, start, end, basedOnExamination, basedOnTelephoneContact));	
		for(int i=20; i<70; i=i+10){
			rowResults.add(getRowResultByAge(i, i+9, start, end, basedOnExamination, basedOnTelephoneContact));
		}
		rowResults.add(getRowResultByAge(70, 150, start, end, basedOnExamination, basedOnTelephoneContact));
		
		return rowResults;
	}
	
	private RowResult getRowResultByAge(Integer minAge, Integer maxAge, long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final int count_male = (int)this.certificateRepository.findCountBySearchAndAge(minAge, maxAge, "Male", start, end, basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountBySearchAndAge(minAge, maxAge, "Female", start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult("" + minAge + "-" + maxAge, count_male, count_female);
		if (150 == maxAge){
			row.setxValue(">" + minAge);
		}else if (0 == minAge){
			row.setxValue("<" + maxAge);
		}
		
		return row;
	}
	
	private List<RowResult> getRowResultsByMonth(DateEntity start, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();
		final Calendar cal = Calendar.getInstance();
		
		cal.setTime(start.getMonthStart());
		for(int i=0; i<12; i++){
			final DateEntity month = this.dateRepository.findByCalendarDate(cal.getTime());
			rowResults.add(getRowResultByMonth(month, basedOnExamination, basedOnTelephoneContact));

			cal.add(Calendar.MONTH, 1);
		}
		
		return rowResults;
	}
	
	private RowResult getRowResultByMonth(DateEntity month, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final int count_male = (int)this.certificateRepository.findCountByMonth("Male", month.getMonthStart(), basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountByMonth("Female", month.getMonthStart(), basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult("" + month.getMonthName(), count_male, count_female);
		
		return row;
	}
	
	private List<RowResult> getRowResultsByCareUnit(long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		List<CareUnitEntity> careUnits = this.careUnitRepository.findAll();
		for (CareUnitEntity careUnit : careUnits){
			rowResults.add(getRowResultByCareUnit(careUnit, start, end, basedOnExamination, basedOnTelephoneContact));
		}

		return rowResults;
	}
	
	private RowResult getRowResultByCareUnit(CareUnitEntity careUnit, long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final int count_male = (int)this.certificateRepository.findCountByCareUnit(careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountByCareUnit(careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult("" + careUnit.getName(), count_male, count_female);
		
		return row;
	}

}
