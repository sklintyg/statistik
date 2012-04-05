package se.inera.statistics.core.spi.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.inera.statistics.core.api.MedicalCertificate;
import se.inera.statistics.core.repository.MedicalCertificateRepository;
import se.inera.statistics.core.spi.StatisticsService;
import se.inera.statistics.model.entity.MedicalCertificateEntity;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
	private MedicalCertificateRepository repo;
	
	@Override
	public List<MedicalCertificate> loadBySearch(MedicalCertificate search) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			final Date start = sdf.parse(search.getStartDate());
			final Date end = sdf.parse(search.getEndDate());
			
			final List<MedicalCertificateEntity> ents = this.repo.loadBySearch(
					start, 
					end, search.isBasedOnExamination(), search.isBasedOnTelephoneContact());
			
			return MedicalCertificate.newFromEntities(ents);
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<MedicalCertificate> loadMesasureThreeStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificate> loadMeasureNineStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificate> loadMeasureTenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificate> loadMeasureElevenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificate> loadMeasureTwelveStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public List<MedicalCertificate> loadMeasureFifteenStatistics() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
