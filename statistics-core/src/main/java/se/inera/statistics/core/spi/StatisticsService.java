package se.inera.statistics.core.spi;

import java.util.List;

import se.inera.commons.support.ServiceResult;
import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.api.StatisticsResult;

public interface StatisticsService {
	
	/**
	 * Load certificates based on search criterion
	 * @param search
	 * @return
	 */
	ServiceResult<StatisticsResult> loadBySearch(final MedicalCertificateDto search);

	/**
	 * Measure 3:
	 * Number of certificates with documented description of
	 * anamnes, examination results and limited activity
	 */
	List<MedicalCertificateDto> loadMesasureThreeStatistics();
	
	/**
	 * Measure 9:
	 * Number of certificates with a diagnose according to ICD-10
	 */
	List<MedicalCertificateDto> loadMeasureNineStatistics();
	
	/**
	 * Measure 10:
	 * Number of certificates that are based on a personal contact with
	 * the patient
	 */
	List<MedicalCertificateDto> loadMeasureTenStatistics();
	
	/**
	 * Measure 11:
	 * Number of certificates that are based on the doctor's examination
	 * of the patient and/or telephone consultation with the patient
	 */
	List<MedicalCertificateDto> loadMeasureElevenStatistics();
	
	/**
	 * Measure 12:
	 * Number of certificates that are based on the doctor's examination
	 * of the patient without any telephone consultation with the patient
	 */
	List<MedicalCertificateDto> loadMeasureTwelveStatistics();
	
	/**
	 * Measure 15:
	 * Number of partly sick leaves for some diagnose groups, grouped by
	 * gender
	 */
	List<MedicalCertificateDto> loadMeasureFifteenStatistics();
}
