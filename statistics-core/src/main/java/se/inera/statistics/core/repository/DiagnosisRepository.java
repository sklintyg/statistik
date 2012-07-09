package se.inera.statistics.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.inera.statistics.model.entity.DiagnosisEntity;

public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long> {
	DiagnosisEntity findByIcd10(String Icd10);
}