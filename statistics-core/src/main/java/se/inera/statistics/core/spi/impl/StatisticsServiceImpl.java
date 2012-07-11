package se.inera.statistics.core.spi.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import se.inera.statistics.core.api.PeriodResult;
import se.inera.statistics.core.api.StatisticsResult;
import se.inera.statistics.core.api.StatisticsViewRange;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.spi.StatisticsService;
import se.inera.statistics.model.entity.MedicalCertificateEntity;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

	private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);
	
	@Autowired
	private MedicalCertificateRepository certificateRepository;
	
	@Autowired
	private DateRepository dateRepository;
	
	@Override
	public ServiceResult<StatisticsResult> loadBySearch(MedicalCertificateDto search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			final long start = this.dateRepository.findByCalendarDate(sdf.parse(search.getStartDate())).getId();
			final long end = this.dateRepository.findByCalendarDate(sdf.parse(search.getEndDate())).getId();
			
			final List<MedicalCertificateEntity> total = this.certificateRepository.findCertificatesInRange(start, end);
			final List<MedicalCertificateEntity> matches = this.certificateRepository.findBySearch(start, end, search.getBasedOnExamination(), search.getBasedOnTelephoneContact());
			
			/*
			 * Slice the result
			 */
			return ServiceResultImpl.newSuccessfulResult(this.processResults(StatisticsViewRange.fromCode(search.getViewRange()), total, matches), Collections.singletonList(new DefaultServiceMessage("Test", ServiceMessageType.SUCCESS)));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private StatisticsResult processResults(final StatisticsViewRange range
			, final List<MedicalCertificateEntity> totals
			, final List<MedicalCertificateEntity> matches) throws Exception {
		
		final StatisticsResult result = new StatisticsResult(matches.size(), totals.size(), range);
		int type;
		switch (result.getView()) {
		case DAILY:
			type = Calendar.DAY_OF_YEAR;
			break;
		case WEEKLY:
			type = Calendar.WEEK_OF_YEAR;
			break;
		case MONTHLY:
			type = Calendar.MONTH;
			break;
		case YEARLY:
			type = Calendar.YEAR;
			break;
		default:
			type = Calendar.YEAR;
		}
		result.setMatches(this.getStatsFromCollection(matches, type));
		result.setTotals(this.getStatsFromCollection(totals, type));
		
		return result;
	}
	
	private List<PeriodResult> getStatsFromCollection(final List<MedicalCertificateEntity> list, final int period) throws Exception {
		final List<PeriodResult> result = new ArrayList<PeriodResult>();
		
		if (!list.isEmpty()) {
			final Calendar cal = Calendar.getInstance();
			final Calendar current = Calendar.getInstance();
			PeriodResult currentPeriod = null;
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate;
			
			log.debug("Processing entity list");
			for (MedicalCertificateEntity e: list) {
				
				if (currentPeriod == null) {
					startDate = this.dateRepository.findOne(e.getStartDate()).getCalendarDate();
					cal.setTime(startDate);					
					currentPeriod = PeriodResult.newResult(startDate);
				}
				
				current.setTime(this.dateRepository.findOne(e.getStartDate()).getCalendarDate());
				
				log.debug("Comparing {} with {}", sdf.format(cal.getTime()), sdf.format(current.getTime()));
				if (cal.get(period) == current.get(period)) {
					currentPeriod.increaseValue();								
				} else {
					/*
					 * Calculation finished - add
					 */
					currentPeriod.setEnd(cal.getTime());
					result.add(currentPeriod);
					log.debug("Adding period starting at {} with count {}", array(currentPeriod.getStart(), currentPeriod.getValue()));
					
					/*
					 * Reset counters
					 */
					startDate = this.dateRepository.findOne(e.getStartDate()).getCalendarDate();
					cal.setTime(startDate);
					currentPeriod = PeriodResult.newResult(startDate);
					currentPeriod.increaseValue();
				}
			}
			
			// add last group
			currentPeriod.setEnd(cal.getTime());
			result.add(currentPeriod);
			log.debug("Adding period starting at {} with count {}", array(currentPeriod.getStart(), currentPeriod.getValue()));
		}
		
		log.debug("Found {} periods in total.", result.size());
		return result;
	}
	
	

	private Object[] array(Object...data) {
		return data;
	}

	@Override
	public List<MedicalCertificateDto> loadMesasureThreeStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificateDto> loadMeasureNineStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificateDto> loadMeasureTenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificateDto> loadMeasureElevenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificateDto> loadMeasureTwelveStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificateDto> loadMeasureFifteenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
