package se.inera.statistics.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.inera.statistics.model.entity.MedicalCertificateEntity;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificateEntity, Long> {

	String GENDER_MALE = "Male";
	String GENDER_FEMALE = "Female";
	
	@Query("select e from MedicalCertificateEntity as e where " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) order by e.startDate asc")
	List<MedicalCertificateEntity> findCertificatesInRange(@Param("startDate") final long start, @Param("endDate") final long end);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"(p.age >= :minAge and p.age <= :maxAge) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate)")
	long findCountBySearchAndAge(
			@Param("minAge") final int minAge,
			@Param("maxAge") final int maxAge,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"p.gender = :gender and " +
			"(p.age >= :minAge and p.age <= :maxAge) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and d.icd10Group = :icd10Group")
	long findCountByAgeAndIcd10Group(
			@Param("minAge") final int minAge,
			@Param("maxAge") final int maxAge,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("icd10Group") final String icd10Group);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p" +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"(p.age >= :minAge and p.age <= :maxAge) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and e.workDisability = :workDisability")
	long findCountByAgeAndWorkDisability(
			@Param("minAge") final int minAge,
			@Param("maxAge") final int maxAge,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("workDisability") final int workDisability);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"p.gender = :gender and " +
			"(p.age >= :minAge and p.age <= :maxAge) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and d.icd10Group = :icd10Group and e.workDisability = :workDisability")
	long findCountByAgeAndIcd10GroupAndWorkDisability(
			@Param("minAge") final int minAge,
			@Param("maxAge") final int maxAge,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("icd10Group") final String icd10Group,
			@Param("workDisability") final int workDisability);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"(p.age >= :minAge and p.age <= :maxAge) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountBySearchAndAge(
			@Param("minAge") final int minAge,
			@Param("maxAge") final int maxAge,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"((e.endDate - e.startDate) >= :minDuration and (e.endDate - e.startDate) <= :maxDuration) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate)")
	long findCountByDuration(
			@Param("minDuration") final long minDuration,
			@Param("maxDuration") final long maxDuration,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"p.gender = :gender and " +
			"((e.endDate - e.startDate) >= :minDuration and (e.endDate - e.startDate) <= :maxDuration) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and d.icd10Group = :icd10Group")
	long findCountByDurationAndIcd10Group(
			@Param("minDuration") final long minDuration,
			@Param("maxDuration") final long maxDuration,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("icd10Group") final String icd10Group);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"((e.endDate - e.startDate) >= :minDuration and (e.endDate - e.startDate) <= :maxDuration) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and e.workDisability = :workDisability")
	long findCountByDurationAndWorkDisability(
			@Param("minDuration") final long minDuration,
			@Param("maxDuration") final long maxDuration,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("workDisability") final int workDisability);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"p.gender = :gender and " +
			"((e.endDate - e.startDate) >= :minDuration and (e.endDate - e.startDate) <= :maxDuration) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and d.icd10Group = :icd10Group and e.workDisability = :workDisability")
	long findCountByDurationAndIcd10GroupAndWorkDisability(
			@Param("minDuration") final long minDuration,
			@Param("maxDuration") final long maxDuration,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("icd10Group") final String icd10Group,
			@Param("workDisability") final int workDisability);


	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, " +
			"DateEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.startDate = d.id and " +
			"p.gender = :gender and " +
			"d.monthStart = :monthStart")
	long findCountByMonth(
			@Param("gender") final String gender,
			@Param("monthStart") final Date monthStart);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, " +
			"DateEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.startDate = d.id and " +
			"p.gender = :gender and " +
			"d.monthStart = :monthStart and e.workDisability = :workDisability")
	long findCountByMonthAndDisability(
			@Param("gender") final String gender,
			@Param("monthStart") final Date monthStart,
			@Param("workDisability") final int workDisability);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d, " +
			"DateEntity as de " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"e.startDate = de.id and " +
			"p.gender = :gender and " +
			"de.monthStart = :monthStart and d.icd10Group = :icd10Group")
	long findCountByMonthAndIcd10Group(
			@Param("gender") final String gender,
			@Param("monthStart") final Date monthStart,
			@Param("icd10Group") final String icd10Group);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, DiagnosisEntity as d, " +
			"DateEntity as de " +
			" where " +
			"e.personId = p.id and " +
			"e.diagnosisId = d.id and " +
			"e.startDate = de.id and " +
			"p.gender = :gender and " +
			"de.monthStart = :monthStart and d.icd10Group = :icd10Group and e.workDisability = :workDisability")
	long findCountByMonthAndIcd10GroupAndWorkDisability(
			@Param("gender") final String gender,
			@Param("monthStart") final Date monthStart,
			@Param("icd10Group") final String icd10Group,
			@Param("workDisability") final int workDisability);

	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"e.diagnosisId in (:diagnosisIds) and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountByDiagnosisGroup(
			@Param("gender") final String gender,
			@Param("diagnosisIds") final List<Long> diagnosisIds,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
	
	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p " +
			" where " +
			"e.personId = p.id and " +
			"p.gender = :gender and " +
			"e.careUnitId = :careUnitId and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountByCareUnit(
			@Param("gender") final String gender,
			@Param("careUnitId") final long careUnitId,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
}
