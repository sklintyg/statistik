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

	private static final String JSON = "application/json";
    @Autowired
	private StatisticsService service;
	
	@RequestMapping(value="/age", method=RequestMethod.POST, consumes=JSON, produces=JSON)
	@ResponseBody
	public ServiceResult<StatisticsResult> loadAgeStatistics(@RequestBody final AgeForm criterias) {
		return this.service.loadByAge(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability(), criterias.getGroup());
	}
	
	@RequestMapping(value="/duration", method=RequestMethod.POST, consumes=JSON, produces=JSON)
	@ResponseBody
	public ServiceResult<StatisticsResult> loadDurationStatistics(@RequestBody final DurationForm criterias) {
		return this.service.loadStatisticsByDuration(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability(), criterias.getGroup());
	}
	
	@RequestMapping(value="/monthwise", method=RequestMethod.POST, consumes=JSON, produces=JSON)
	@ResponseBody
	public ServiceResult<StatisticsResult> loadMonthwiseStatistics(@RequestBody final MonthwiseForm criterias) {
		return this.service.loadStatisticsByMonth(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability(), criterias.getGroup());
	}
	
	@RequestMapping(value="/diagnosisgroups", method=RequestMethod.POST, consumes=JSON, produces=JSON)
	@ResponseBody
	public ServiceResult<StatisticsResult> loadDiagnosisGroupsStatistics(@RequestBody final DiagnosisForm criterias) {
		return this.service.loadStatisticsByDiagnosisGroups(criterias.getFromDate(), criterias.getToDate(), criterias.getDisability());
	}
	
	@RequestMapping(value="/careunit", method=RequestMethod.POST, consumes=JSON, produces=JSON)
	@ResponseBody
	public ServiceResult<StatisticsResult> loadCareUnitStatistics(@RequestBody final MedicalCertificateDto criterias) {
		return this.service.loadStatisticsByCareUnit(criterias);
	}
}
