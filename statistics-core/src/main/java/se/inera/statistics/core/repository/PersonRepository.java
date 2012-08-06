package se.inera.statistics.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.inera.statistics.model.entity.PersonEntity;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
	PersonEntity findByAgeAndGender(int age, String gender);
}