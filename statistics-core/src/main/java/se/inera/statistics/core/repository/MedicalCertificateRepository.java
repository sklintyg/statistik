package se.inera.statistics.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import se.inera.statistics.model.entity.MedicalCertificateEntity;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificateEntity, Long> {

	@Query(value="select e from MedicalCertificateEntity as e where " +
			"(e.startDate >= :startDate and e.endDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact order by e.startDate asc")
	List<MedicalCertificateEntity> findBySearch(
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
	
	@Query("select e from MedicalCertificateEntity as e where " +
			"(e.startDate >= :startDate and e.endDate <= :endDate) order by e.startDate asc")
	List<MedicalCertificateEntity> findCertificatesInRange(@Param("startDate") final long start, @Param("endDate") final long end);
	

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
			"(e.startDate >= :startDate and e.startDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountByDuration(
			@Param("minDuration") final long minDuration,
			@Param("maxDuration") final long maxDuration,
			@Param("gender") final String gender,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
	
	@Query(value="select count(e.id) from MedicalCertificateEntity as e, " +
			"PersonEntity as p, " +
			"DateEntity as d " +
			" where " +
			"e.personId = p.id and " +
			"e.startDate = d.id and " +
			"p.gender = :gender and " +
			"d.monthStart = :monthStart and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountByMonth(
			@Param("gender") final String gender,
			@Param("monthStart") final Date monthStart,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
	
	@Query(value="select count(e.id) from MedicalCertificateEntity as e " +
			" where " +
			"e.careUnitId = :careUnitId and " +
			"(e.startDate >= :startDate and e.startDate <= :endDate) and " +
			"e.basedOnExamination = :basedOnExamination and " +
			"e.basedOnTelephoneContact = :basedOnTelephoneContact")
	long findCountByCareUnit(
			@Param("careUnitId") final long careUnitId,
			@Param("startDate") final long start,
			@Param("endDate") final long end,
			@Param("basedOnExamination") final Boolean basedOnExamination,
			@Param("basedOnTelephoneContact") final Boolean basedOnTelephoneContact);
}
