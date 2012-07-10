package se.inera.statistics.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.inera.statistics.model.entity.CareUnitEntity;

public interface CareUnitRepository extends JpaRepository<CareUnitEntity, Long> {
	CareUnitEntity findByName(final String name);
}
