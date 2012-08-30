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
import se.inera.statistics.core.repository.CareUnitRepository;
import se.inera.statistics.core.repository.DateRepository;
import se.inera.statistics.core.repository.DiagnosisRepository;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.repository.PersonRepository;
import se.inera.statistics.core.spi.RegisterStatisticsService;
import se.inera.statistics.model.entity.CareUnitEntity;
import se.inera.statistics.model.entity.DateEntity;
import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.IcdGroup;
import se.inera.statistics.model.entity.IcdGroupList;
import se.inera.statistics.model.entity.MedicalCertificateEntity;
import se.inera.statistics.model.entity.PersonEntity;
import se.inera.statistics.model.entity.WorkCapability;

@Service
@Transactional
public class RegisterStatisticsServiceImpl implements RegisterStatisticsService {

	private static final Logger log = LoggerFactory.getLogger(RegisterStatisticsServiceImpl.class);
	
	@Autowired
	private MedicalCertificateRepository certificateRepository;

	@Autowired
	private PersonRepository personRepository;	
	
	@Autowired
	private DateRepository dateRepository;
	
	@Autowired
	private DiagnosisRepository diagnosisRepository;
	
	@Autowired
	private CareUnitRepository careUnitRepository;
	
	@Autowired
	private IcdGroupList icdGroupList;
	
	@Override
	public boolean registerMedicalCertificateStatistics(
			MedicalCertificateDto certificate) {
		
		try {
			final DateEntity startDate = getDateEntity(certificate.getStartDate());
			final DateEntity endDate = getDateEntity(certificate.getEndDate());
			
			final MedicalCertificateEntity ent = MedicalCertificateEntity.newEntity(startDate.getId(), endDate.getId());
			final PersonEntity person = getPerson(certificate.getAge(), certificate.getFemale());
			final DiagnosisEntity diagnosis = getDiagnosis(certificate.getIcd10(), certificate.getDiagnose(), certificate.getworkDisability());
			
			if (certificate.getCareUnit() == null) {
				throw new NullPointerException("Care unit name is null.");
			}
			
			final CareUnitEntity careUnit = getCareUnit(certificate.getCareUnit());
			
			ent.setPersonId(person.getId());
			ent.setDiagnosisId(diagnosis.getId());
			ent.setCareUnitId(careUnit.getId());
			ent.setBasedOnExamination(certificate.getBasedOnExamination());
			ent.setBasedOnTelephoneContact(certificate.getBasedOnTelephoneContact());
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
	
	private DiagnosisEntity getDiagnosis(final String icd10, final boolean diagnose, final int workDisability){
		WorkCapability workCapability = WorkCapability.fromWorkDisabilityPercentage(workDisability);
		DiagnosisEntity diagnosis = this.diagnosisRepository.findByIcd10AndWorkCapability(icd10, workCapability);
		//TODO: Understand semantics of diagnose boolean and implement strategy for creating/updating the values
		if (null == diagnosis){
			final IcdGroup icdGroup = icdGroupList.getGroup(icd10);
			diagnosis = DiagnosisEntity.newEntity(icd10, diagnose, workCapability, icdGroup.getChapter(), icdGroup.getDescription());
			this.diagnosisRepository.save(diagnosis);
		}
		return diagnosis;
	}
	
	private DateEntity getDateEntity(final String dateString) throws ParseException{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Date calendarDate = sdf.parse(dateString);
		
		DateEntity dateEntity = this.dateRepository.findByCalendarDate(calendarDate);
		if (null == dateEntity){
			throw new IllegalArgumentException("Cannot find date Id for date " + calendarDate.toString() + ".");
		}
		return dateEntity;
	}
	
	private CareUnitEntity getCareUnit(final String name){
		CareUnitEntity careUnit = this.careUnitRepository.findByName(name);
		if (null == careUnit){
			careUnit = CareUnitEntity.newEntity(name);
			this.careUnitRepository.save(careUnit);
		}
		return careUnit;
	}
	
	private String getGender(boolean female){
		//TODO: move the enums to better place
		return female ? MedicalCertificateRepository.GENDER_FEMALE: MedicalCertificateRepository.GENDER_MALE;
	}
}
