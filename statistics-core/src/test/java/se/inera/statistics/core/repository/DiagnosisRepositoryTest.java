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
		List<String> result = this.diagnosisRepository.findAllSicknessGroups();
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
