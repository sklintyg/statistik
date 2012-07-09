package se.inera.statistics.core.spi.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.core.spi.RegisterStatisticsService;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;

@Service
@Transactional
public class RegisterStatisticsServiceImpl implements RegisterStatisticsService {

	private static final Logger log = LoggerFactory.getLogger(RegisterStatisticsServiceImpl.class);
	
	@Autowired
	private MedicalCertificateRepository certificateRepository;

	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	private DiagnosisRepository diagnosisRepository;
	
	@Override
	public boolean registerMedicalCertificateStatistics(
			MedicalCertificateDto certificate) {
		
		//final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			final Date start = sdf.parse(certificate.getStartDate());
			final Date end = sdf.parse(certificate.getEndDate());
			

			final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(start, end);
			final PersonEntity person = getPerson(certificate.getAge(), certificate.isFemale());
			final DiagnosisEntity diagnosis = getDiagnosis(certificate.getIcd10(), certificate.isDiagnose());
			
			ent.setPersonId(person.getId());
			ent.setDiagnosisId(diagnosis.getId());
			ent.setBasedOnExamination(certificate.isBasedOnExamination());
			ent.setBasedOnTelephoneContact(certificate.isBasedOnTelephoneContact());
//			ent.setIcd10(certificate.getIcd10());
			this.certificateRepository.save(ent);
			
			log.info("Medical certificate statistics data successfully registered.");
			return true;
		} catch (final ParseException e) {
			log.error("Unable to parse incoming dates", e);
			return false;
		}
	}
	
	private PersonEntity getPerson(final int age, final boolean female){
		PersonEntity person = this.personRepository.findByAgeAndGender(age, getGender(female));
		if (null == person){
			person = PersonEntity.newEntity(age, getGender(female));
			this.personRepository.save(person);
		}
		return person;
	}
	
	private DiagnosisEntity getDiagnosis(final String Icd10, final boolean diagnose){
		DiagnosisEntity diagnosis = this.diagnosisRepository.findByIcd10(Icd10);
		//TODO: Understand semantics of diagnose boolean and implement strategy for creating/updating the values
		if (null == diagnosis){
			diagnosis = DiagnosisEntity.newEntity(Icd10, diagnose);
			this.diagnosisRepository.save(diagnosis);
		}
		return diagnosis;
	}
	
	private String getGender(boolean female){
		//TODO: move the enums to better place
		return female ? "Female": "Male";
	}
}
