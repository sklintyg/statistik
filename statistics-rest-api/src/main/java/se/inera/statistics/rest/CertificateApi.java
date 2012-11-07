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
