package se.inera.statistics.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.inera.commons.support.ServiceResult;
import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.api.StatisticsResult;
import se.inera.statistics.core.spi.StatisticsService;

@Controller
@RequestMapping(value="/statistics")
public class StatisticsApi {

	@Autowired
	private StatisticsService service;
	
	@RequestMapping(value="/age", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<StatisticsResult> loadAgeStatistics(@RequestBody final AgeForm criterias) {
		return this.service.loadByAge(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability(), criterias.getGroup());
	}
	
	@RequestMapping(value="/duration", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<StatisticsResult> loadDurationStatistics(@RequestBody final DurationForm criterias) {
		return this.service.loadStatisticsByDuration(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability(), criterias.getGroup());
	}
	
	@RequestMapping(value="/monthwise", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<StatisticsResult> loadMonthwiseStatistics(@RequestBody final MedicalCertificateDto criterias) {
		return this.service.loadStatisticsByMonth(criterias);
	}
	
	@RequestMapping(value="/sicknessgroups", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<StatisticsResult> loadSicknessGroupsStatistics(@RequestBody final MedicalCertificateDto criterias) {
		return this.service.loadStatisticsBySicknessGroups(criterias);
	}
	
	@RequestMapping(value="/careunit", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public ServiceResult<StatisticsResult> loadCareUnitStatistics(@RequestBody final MedicalCertificateDto criterias) {
		return this.service.loadStatisticsByCareUnit(criterias);
	}
}
