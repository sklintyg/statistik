package se.inera.statistics.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.spi.RegisterStatisticsService;

@Controller
@RequestMapping(value="/certificate")
public class CertificateApi {

	@Autowired
	private RegisterStatisticsService service;
	
	@RequestMapping(value="/medical/register", method=RequestMethod.POST, consumes="application/json", produces="application/json")
	@ResponseBody
	public Boolean registerMedicalCertificate(@RequestBody final MedicalCertificateDto certificate) {
		return this.service.registerMedicalCertificateStatistics(certificate);
	}
	
	@RequestMapping(value="/test", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String test() {
		return "Success";
	}
}
