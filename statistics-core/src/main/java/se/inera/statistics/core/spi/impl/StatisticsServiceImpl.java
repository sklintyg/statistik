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
package se.inera.statistics.core.spi.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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

	private static final String FEMALE = "Female";

    private static final String MALE = "Male";

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
		final long start = getStartDate(from);
		final long end = getEndDate(to);

		final StatisticsResult result = new StatisticsResult(this.getRowResultsByDuration(start, end, disability, group));
		
		return ok(result);
	}

	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByMonth(String from, String to, String disability, String group) {
		final long start = getStartDate(from);
		final long end = getEndDate(to);

		final StatisticsResult result = new StatisticsResult(this.getRowResultsByMonth(start, end, disability, group));
		
		return ok(result);
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByDiagnosisGroups(final MedicalCertificateDto search) {
		final long start = getStartDate(search.getStartDate());
		final long end = getEndDate(search.getEndDate());

		final StatisticsResult result = new StatisticsResult(this.getRowResultsByDiagnosisGroups(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
		
		return ok(result);
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadStatisticsByCareUnit(final MedicalCertificateDto search) {
		final long start = getStartDate(search.getStartDate());
		final long end = getEndDate(search.getEndDate());

		final StatisticsResult result = new StatisticsResult(this.getRowResultsByCareUnit(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact()));
			
		return ok(result);
	}
	
	@Override
	public ServiceResult<StatisticsResult> loadByAge(String from, String to, String disability, String group) {
		final StatisticsResult result = new StatisticsResult(getRowResultsByAge(getStartDate(from), getEndDate(to), disability, group));
			
		return ok(result);
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
		final long countMale;
		final long countFemale;

		if (isAllSelected(disability)) {
			if (isAllSelected(group)) {
				countMale= this.certificateRepository.findCountByDuration(minDuration, maxDuration, MALE, start, end);
				countFemale= this.certificateRepository.findCountByDuration(minDuration, maxDuration, FEMALE, start, end);
			} else {
				countMale= this.certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, MALE, start, end, group);
				countFemale= this.certificateRepository.findCountByDurationAndIcd10Group(minDuration, maxDuration, FEMALE, start, end, group);
			}
		} else {
			int disabilityInt = Integer.parseInt(disability);
			if (isAllSelected(group)) {
				countMale= this.certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, MALE, start, end, disabilityInt);
				countFemale= this.certificateRepository.findCountByDurationAndWorkDisability(minDuration, maxDuration, FEMALE, start, end, disabilityInt);
			} else {
				countMale= this.certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, MALE, start, end, group, disabilityInt);
				countFemale= this.certificateRepository.findCountByDurationAndIcd10GroupAndWorkDisability(minDuration, maxDuration, FEMALE, start, end, group, disabilityInt);
			}
		}
		return RowResult.newResult(formatDuration(minDuration, maxDuration), countMale, countFemale);
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
		final long countMale;
		final long countFemale;
		
		if (isAllSelected(group)) {
			if (isAllSelected(disability)) {
				countMale = certificateRepository.findCountBySearchAndAge(minAge, maxAge, MALE, start, end);		
				countFemale = certificateRepository.findCountBySearchAndAge(minAge, maxAge, FEMALE, start, end);
			} else {
				countMale = certificateRepository.findCountByAgeAndWorkDisability(minAge, maxAge, MALE, start, end, Integer.parseInt(disability));		
				countFemale = certificateRepository.findCountByAgeAndWorkDisability(minAge, maxAge, FEMALE, start, end, Integer.parseInt(disability));
			}
		} else {
			if (isAllSelected(disability)) {
				countMale = certificateRepository.findCountByAgeAndIcd10Group(minAge, maxAge, MALE, start, end, group);		
				countFemale = certificateRepository.findCountByAgeAndIcd10Group(minAge, maxAge, FEMALE, start, end, group);
			} else {
				countMale = certificateRepository.findCountByAgeAndIcd10GroupAndWorkDisability(minAge, maxAge, MALE, start, end, group, Integer.parseInt(disability));		
				countFemale = certificateRepository.findCountByAgeAndIcd10GroupAndWorkDisability(minAge, maxAge, FEMALE, start, end, group, Integer.parseInt(disability));
			}
		}
		return RowResult.newResult(formatAgeRange(minAge, maxAge), countFemale, countMale);
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

	private List<RowResult> getRowResultsByMonth(long start, long end, String disability, String group){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		final Calendar current = Calendar.getInstance();
		final Calendar last = Calendar.getInstance();
		current.setTime(dateRepository.findOne(start).getMonthStart());
		last.setTime(dateRepository.findOne(end).getMonthEnd());
		
		
		while(current.before(last)){
			final DateEntity month = dateRepository.findByCalendarDate(current.getTime());
			rowResults.add(getRowResultByMonth(month, disability, group));
			current.add(Calendar.MONTH, 1);
		}
		
		return rowResults;
	}
	
	private RowResult getRowResultByMonth(DateEntity month, String disability, String group){
		final long countMale;
		final long countFemale;
		if (isAllSelected(disability)) {
			if (isAllSelected(group)) {
				countMale = certificateRepository.findCountByMonth(MALE, month.getMonthStart());
				countFemale = certificateRepository.findCountByMonth(FEMALE, month.getMonthStart());
			} else {
				countMale = certificateRepository.findCountByMonthAndIcd10Group(MALE, month.getMonthStart(), group);
				countFemale = certificateRepository.findCountByMonthAndIcd10Group(FEMALE, month.getMonthStart(), group);
			}
		} else {
			int workDisability = Integer.parseInt(disability);
			if (isAllSelected(group)) {
				countMale = certificateRepository.findCountByMonthAndDisability(MALE, month.getMonthStart(), workDisability);
				countFemale = certificateRepository.findCountByMonthAndDisability(FEMALE, month.getMonthStart(), workDisability);
			} else {
				countMale = certificateRepository.findCountByMonthAndIcd10GroupAndWorkDisability(MALE, month.getMonthStart(), group, workDisability);
				countFemale = certificateRepository.findCountByMonthAndIcd10GroupAndWorkDisability(FEMALE, month.getMonthStart(), group, workDisability);				
			}
		}

		return RowResult.newResult(month.getMonthName(), countMale, countFemale);
	}
	
	private List<RowResult> getRowResultsByDiagnosisGroups(long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		List<RowResult> rowResults = new ArrayList<RowResult>();

		List<String> diagnosisGroups = this.diagnosisRepository.findAllDiagnosisGroups();
		for (String diagnosisGroup : diagnosisGroups){
			List<Long> icd10Ids = this.diagnosisRepository.findIdsByIcd10group(diagnosisGroup);
			rowResults.add(getRowResultByDiagnosisGroup(diagnosisGroup, icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact));
		}
		
		return rowResults;
	}
	
	private RowResult getRowResultByDiagnosisGroup(String diagnosisGroup, List<Long> icd10Ids, long start, long end, Boolean basedOnExamination, Boolean basedOnTelephoneContact){
		final long countMale = this.certificateRepository.findCountByDiagnosisGroup(MALE, icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact);
		final long countFemale = this.certificateRepository.findCountByDiagnosisGroup(FEMALE, icd10Ids, start, end, basedOnExamination, basedOnTelephoneContact);
		RowResult row = RowResult.newResult(diagnosisGroup, countMale, countFemale);

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
		final int countMale = (int)this.certificateRepository.findCountByCareUnit(MALE, careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		final int countFemale = (int)this.certificateRepository.findCountByCareUnit(FEMALE, careUnit.getId(), start, end, basedOnExamination, basedOnTelephoneContact);
		return RowResult.newResult(careUnit.getName(), countMale, countFemale);
	}
	
	private ServiceResult<StatisticsResult> ok(final StatisticsResult result) {
		return ServiceResultImpl.newSuccessfulResult( result, Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS)));
	}	

	private boolean isAllSelected(String disability) {
		return "all".equals(disability);
	}

	private long getStartDate(final String date) {
		final DateEntity startDate = this.dateRepository.findByCalendarDate(parse(date));
		return this.dateRepository.findByCalendarDate(startDate.getMonthStart()).getId();	
	}
	
	private long getEndDate(final String date) {
		final DateEntity endDate = this.dateRepository.findByCalendarDate(parse(date));
		return this.dateRepository.findByCalendarDate(endDate.getMonthEnd()).getId();
	}

	private Date parse(final String date) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEXT_FORMAT, LOCALE);		
			return sdf.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not parse date " + date, e);
		}
	}

}
