package se.inera.statistics.core.spi.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.core.api.MedicalCertificate;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.spi.RegisterStatisticsService;
import se.inera.statistics.model.entity.MedicalCertificateEntity;

@Service
@Transactional
public class RegisterStatisticsServiceImpl implements RegisterStatisticsService {

	private static final Logger log = LoggerFactory.getLogger(RegisterStatisticsServiceImpl.class);
	
	@Autowired
	private MedicalCertificateRepository repo;
	
	@Override
	public boolean registerMedicalCertificateStatistics(
			MedicalCertificate certificate) {
		
		//final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			final Date start = sdf.parse(certificate.getStartDate());
			final Date end = sdf.parse(certificate.getEndDate());
			
			final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(certificate.getAge(), certificate.isFemale(), start, end);
			ent.setBasedOnExamination(certificate.isBasedOnExamination());
			ent.setBasedOnTelephoneContact(certificate.isBasedOnTelephoneContact());
			this.repo.save(ent);
			
			log.info("Medical certificate statistics data successfully registered.");
			return true;
		} catch (final ParseException e) {
			log.error("Unable to parse incoming dates", e);
			return false;
		}
	}
}
