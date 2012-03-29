package se.inera.statistics.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.inera.statistics.model.entity.MedicalCertificateEntity;

public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificateEntity, Long> {

}
