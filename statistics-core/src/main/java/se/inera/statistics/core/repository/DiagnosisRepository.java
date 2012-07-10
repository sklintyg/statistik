package se.inera.statistics.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.WorkCapability;

public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long> {
	DiagnosisEntity findByIcd10AndWorkCapability(String Icd10, WorkCapability workCapability);
}