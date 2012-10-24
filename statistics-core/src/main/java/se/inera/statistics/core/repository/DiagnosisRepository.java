package se.inera.statistics.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.WorkCapability;

public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long> {
	DiagnosisEntity findByIcd10AndWorkCapability(String Icd10, WorkCapability workCapability);
	
	@Query("select e.id from DiagnosisEntity as e " +
			"where e.icd10Group = :icd10Group")
	List<Long> findIdsByIcd10group(
			@Param("icd10Group") final String icd10Group);
	
	//TODO: Look into better ordering. In current form, IX will come before V, VI, VII
	@Query("select e.icd10Group from DiagnosisEntity as e " +
			"group by icd10Group order by icd10Group")
	List<String> findAllDiagnosisGroups();
}