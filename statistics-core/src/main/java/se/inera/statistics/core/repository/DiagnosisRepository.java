/**
 * Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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