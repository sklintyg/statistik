package se.inera.statistics.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.inera.statistics.core.api.MedicalCertificate;
import se.inera.statistics.core.spi.StatisticsService;

@Controller
@RequestMapping(value="/statistics")
public class StatisticsApi {

	@Autowired
	private StatisticsService service;
	
	@RequestMapping(value="/search", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public List<MedicalCertificate> loadStatisticsFromSearch(@RequestBody final MedicalCertificate criterias) {
		return this.service.loadBySearch(criterias);
	}
}
