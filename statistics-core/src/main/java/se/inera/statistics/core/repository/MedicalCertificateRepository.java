package se.inera.statistics.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import se.inera.statistics.model.entity.MedicalCertificateEntity;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificateEntity, Long> {

	@Query(value="select e from MedicalCertificateEntity as e where " +
			"e.diagnose = 1 and " +
			"e.examinationResults = 1 " +
			"and e.actualSicknessProcess = 1")
	List<MedicalCertificateEntity> loadMeasureThreeStatistics();
	
	@Query(value="select e from MedicalCertificateEntity as e where " +
			"e.icd10 is not null")
	List<MedicalCertificateEntity> loadMeasureNineStatistics();
	
	@Query(value="select e from MedicalCertificateEntity as e where " +
			"e.basedOnTelephoneContact = 1")
	List<MedicalCertificateEntity> loadMeasureTenStatistics();
	
	@Query(value="select e from MedicalCertificateEntity as e where " +
			"(e.basedOnExamination = 1 or e.basedOnTelephoneContact = 1)")
	List<MedicalCertificateEntity> loadMeasureElevenStatistics();
	
	@Query(value="select e from MedicalCertificateEntity as e where " +
			"(e.basedOnExamination = 0 and e.basedOnTelephoneContact = 1)")
	List<MedicalCertificateEntity> loadMeasureTwelveStatistics();
}
