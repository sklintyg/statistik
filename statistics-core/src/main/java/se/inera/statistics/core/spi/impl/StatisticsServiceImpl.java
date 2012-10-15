package se.inera.statistics.core.spi.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.spi.StatisticsService;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DateEntity;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

	private static final int MIN_AGE = 0;

	private static final int MAX_AGE = 150;

	private static final int[] AGE_RANGES = { MIN_AGE, 20, 30, 40, 50 , 60, 70, MAX_AGE };
	
	private static final String TIME_TEXT_FORMAT = "MMM yy";

	private static final Locale LOCALE = new Locale("sv");

	@Autowired
	private MedicalCertificateRepository certificateRepository;
	
	@Autowired
	private DateRepository dateRepository;
	
	@Autowired
	private DiagnosisRepository diagnosisRepository;
	
	@Autowired
	private CareUnitRepository careUnitRepository;
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByDuration(String from, String to, String disability, String group) {
		try {
			final long start = getStartDate(from);
			final long end = getEndDate(to);

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByDuration(start, end, disability, group));
			
			return ok(result);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByMonth(final MedicalCertificateDto search) {
		try {
			final long start = getStartDate(search.getStartDate());

			final StatisticsResult result = null;//new StatisticsResult(this.getRowResultsByMonth(start, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ok(result);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsBySicknessGroups(final MedicalCertificateDto search) {
		try {
			final long start = getStartDate(search.getStartDate());
			final long end = getEndDate(search.getEndDate());

			final StatisticsResult result = new StatisticsResult(this.getRowResultsBySicknessGroups(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ok(result);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByCareUnit(final MedicalCertificateDto search) {
		try {
			final long start = getStartDate(search.getStartDate());
			final long end = getEndDate(search.getEndDate());

			final StatisticsResult result = new StatisticsResult(this.getRowResultsByCareUnit(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
			return ok(result);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadByAge(String from, String to, String disability, String group) {
		try {
			final StatisticsResult result = new StatisticsResult(getRowResultsByAge(getStartDate(from), getEndDate(to), disability, group));
			
			return ok(result);

		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	private List<RowResult> getRowResultsByDuration(long start, long end, String disability, String group){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		rowResults.add(getRowResultByDuration(0, 14, start, end, disability, group));
		rowResults.add(getRowResultByDuration(15, 30, start, end, disability, group));
		rowResults.add(getRowResultByDuration(31, 90, start, end, disability, group));
		rowResults.add(getRowResultByDuration(91, 360, start, end, disability, group));
		// TODO How to handle longer than 360 days? Limit for "a lot of days"?

		
		return rowResults;
	}
	
	private RowResult getRowResultByDuration(long minDuration, long maxDuration, long start, long end, String disability, String group){
		final long count_male;
		final long count_female;

		if ("all".equals(disability)) {
			if ("all".equals(group)) {
				count_male= this.certificateRepository.findCountByDuration(minDuration, maxDuration, "Male", start, end);
				count_female= this.certificateRepository.findCountByDuration(minDuration, maxDuration, "Female", start, end);
			} else {
				count_male= this.certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, "Male", start, end, group);
				count_female= this.certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, "Female", start, end, group);
			}
		} else {
			int disabilityInt = Integer.parseInt(disability);
			if ("all".equals(group)) {
				count_male= this.certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, "Male", start, end, disabilityInt);
				count_female= this.certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, "Female", start, end, disabilityInt);
			} else {
				count_male= this.certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, "Male", start, end, group, disabilityInt);
				count_female= this.certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, "Female", start, end, group, disabilityInt);
			}
		}
		RowResult row = RowResult.newResult(formatDuration(minDuration, maxDuration), count_male, count_female);

		return row;
	}

	private String formatDuration(long minDuration, long maxDuration) {
		if (0 == minDuration){
			return "<" + maxDuration;
		} else {
			return minDuration + "-" + maxDuration;
		}
	}
	
	private List<RowResult> getRowResultsByAge(long start, long end, String disability, String group){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		for (int ageIndex = 0; ageIndex < AGE_RANGES.length - 1; ageIndex++) {
			rowResults.add(getRowResultByAge(AGE_RANGES[ageIndex], AGE_RANGES[ageIndex + 1] - 1, start, end, disability, group));	
		}
		return rowResults;
	}
	
	private RowResult getRowResultByAge(int minAge, int maxAge, long start, long end, String disability, String group) {
		final long count_male;
		final long count_female;
		
		if ("all".equals(group)) {
			if ("all".equals(disability)) {
				count_male = certificateRepository.findCountBySearchAndAge(minAge, maxAge, "Male", start, end);		
				count_female = certificateRepository.findCountBySearchAndAge(minAge, maxAge, "Female", start, end);
			} else {
				count_male = certificateRepository.findCountByAgeAndWorkDisability(minAge, maxAge, "Male", start, end, Integer.parseInt(disability));		
				count_female = certificateRepository.findCountByAgeAndWorkDisability(minAge, maxAge, "Female", start, end, Integer.parseInt(disability));
			}
		} else {
			if ("all".equals(disability)) {
				count_male = certificateRepository.findCountByAgeAndIcd10Group(minAge, maxAge, "Male", start, end, group);		
				count_female = certificateRepository.findCountByAgeAndIcd10Group(minAge, maxAge, "Female", start, end, group);
			} else {
				count_male = certificateRepository.findCountByAgeAndIcd10GroupAndWorkDisability(minAge, maxAge, "Male", start, end, group, Integer.parseInt(disability));		
				count_female = certificateRepository.findCountByAgeAndIcd10GroupAndWorkDisability(minAge, maxAge, "Female", start, end, group, Integer.parseInt(disability));
			}
		}
		return RowResult.newResult(formatAgeRange(minAge, maxAge), count_female, count_male);
	}
	
	private String formatAgeRange(int minAge, int maxAge) {
		if (MAX_AGE == maxAge + 1){
			return ">" + minAge;
		} else if (MIN_AGE == minAge){
			return "<" + maxAge;
		} else {
			return minAge + "-" + maxAge;
		}
	}

	private List<RowResult> getRowResultsByMonth(long start, long end){
		List<RowResult> rowResults = new ArrayList<RowResult>();
		final Calendar cal = Calendar.getInstance();
		
		DateEntity startDate = this.dateRepository.findOne(start);
		cal.setTime(startDate.getMonthStart());
		for(int i=0; i<12; i++){
			final DateEntity month = this.dateRepository.findByCalendarDate(cal.getTime());
			//rowResults.add(getRowResultByMonth(month, basedOnExamination, basedOnTelephoneContact));

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
	
	private List<RowResult> getRowResultsBySicknessGroups(long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		List<String> sicknessGroups = this.diagnosisRepository.findAllSicknessGroups();
		for (String sicknessGroup : sicknessGroups){
			List<Long> icd10Ids = this.diagnosisRepository.findIdsByIcd10group(sicknessGroup);
			rowResults.add(getRowResultBySicknessGroup(sicknessGroup, icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact));
		}
		
		return rowResults;
	}
	
	private RowResult getRowResultBySicknessGroup(String sicknessGroup, List<Long> icd10Ids, long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final int count_male = (int)this.certificateRepository.findCountBySicknessGroup("Male", icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountBySicknessGroup("Female", icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult(sicknessGroup, count_male, count_female);

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
		final int count_male = (int)this.certificateRepository.findCountByCareUnit("Male", careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		final int count_female = (int)this.certificateRepository.findCountByCareUnit("Female", careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult("" + careUnit.getName(), count_male, count_female);
		
		return row;
	}
	
	private ServiceResult<StatisticsResult> ok(final StatisticsResult result) {
		return ServiceResultImpl.newSuccessfulResult( result, Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS)));
	}	

	private long getStartDate(final String date) throws ParseException{
		final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
		
		final DateEntity startDate = this.dateRepository.findByCalendarDate(sdf.parse(date));
		return this.dateRepository.findByCalendarDate(startDate.getMonthStart()).getId();	
	}
	
	private long getEndDate(final String date) throws ParseException{
		final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);
		
		final DateEntity endDate = this.dateRepository.findByCalendarDate(sdf.parse(date));
		return this.dateRepository.findByCalendarDate(endDate.getMonthEnd()).getId();
	}

}
