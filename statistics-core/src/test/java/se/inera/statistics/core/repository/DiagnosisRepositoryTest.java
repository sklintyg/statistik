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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.statistics.model.entity.DiagnosisEntity;
import se.inera.statistics.model.entity.WorkCapability;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:statistics-config.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public class DiagnosisRepositoryTest {

	@Autowired 
	private DiagnosisRepository diagnosisRepository;
	
	@Before
	public void setUp(){
		this.diagnosisRepository.deleteAll();
		
		DiagnosisEntity diagnosis = DiagnosisEntity.newEntity("C544", false, WorkCapability.HALF_WORKING_CAPABILITY, "II", "Tumörer");
		this.diagnosisRepository.save(diagnosis);
		DiagnosisEntity diagnosis2 = DiagnosisEntity.newEntity("L450", false, WorkCapability.FULL_WORKING_CAPABILITY, "XII", "Hudens och underhudens sjukdomar");
		this.diagnosisRepository.save(diagnosis2);
		DiagnosisEntity diagnosis3 = DiagnosisEntity.newEntity("L451", false, WorkCapability.FULL_WORKING_CAPABILITY, "XII", "Hudens och underhudens sjukdomar");
		this.diagnosisRepository.save(diagnosis3);
	}
	
	@Test
	public void testFindAllSicknessGroups() throws Exception{
		List<String> result = this.diagnosisRepository.findAllDiagnosisGroups();
		assertEquals(2, result.size());
		assertEquals("II", result.get(0));
		assertEquals("XII", result.get(1));
	}
	
	@Test
	public void testFindIcdsByIcd10group() throws Exception{
		List<Long> firstGroupIds = this.diagnosisRepository.findIdsByIcd10group("II");
		assertEquals(1, firstGroupIds.size());
		assertEquals("C544", this.diagnosisRepository.findOne(firstGroupIds.get(0)).getIcd10());
		
		List<Long> secondGroupIds = this.diagnosisRepository.findIdsByIcd10group("XII");
		assertEquals(2, secondGroupIds.size());
		assertEquals("L450", this.diagnosisRepository.findOne(secondGroupIds.get(0)).getIcd10());
		assertEquals("L451", this.diagnosisRepository.findOne(secondGroupIds.get(1)).getIcd10());
	}
}
