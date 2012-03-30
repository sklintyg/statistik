package se.inera.statistics.core.spi;

import java.util.List;

import se.inera.statistics.core.api.MedicalCertificate;

public interface StatisticsService {

	/**
	 * Measure 3:
	 * Number of certificates with documented description of
	 * anamnes, examination results and limited activity
	 */
	List<MedicalCertificate> loadMesasureThreeStatistics();
	
	/**
	 * Measure 9:
	 * Number of certificates with a diagnose according to ICD-10
	 */
	List<MedicalCertificate> loadMeasureNineStatistics();
	
	/**
	 * Measure 10:
	 * Number of certificates that are based on a personal contact with
	 * the patient
	 */
	List<MedicalCertificate> loadMeasureTenStatistics();
	
	/**
	 * Measure 11:
	 * Number of certificates that are based on the doctor's examination
	 * of the patient and/or telephone consultation with the patient
	 */
	List<MedicalCertificate> loadMeasureElevenStatistics();
	
	/**
	 * Measure 12:
	 * Number of certificates that are based on the doctor's examination
	 * of the patient without any telephone consultation with the patient
	 */
	List<MedicalCertificate> loadMeasureTwelveStatistics();
	
	/**
	 * Measure 15:
	 * Number of partly sick leaves for some diagnose groups, grouped by
	 * gender
	 */
	List<MedicalCertificate> loadMeasureFifteenStatistics();
}
