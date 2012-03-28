package se.inera.statistics.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import se.inera.statistics.core.api.MedicalCertificate;

@Controller
@RequestMapping(value="/certificate")
public class CertificateApi {

	@RequestMapping(value="/medical/register", method=RequestMethod.POST)
	public String registerMedicalCertificate(@RequestBody final MedicalCertificate certificate) {
		throw new UnsupportedOperationException();
	}
}
