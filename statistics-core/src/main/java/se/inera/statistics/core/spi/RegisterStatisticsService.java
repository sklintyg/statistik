package se.inera.statistics.core.spi;

import se.inera.statistics.core.api.MedicalCertificate;

public interface RegisterStatisticsService {

	/**
	 * Add a medical certificate statistic data to the storage
	 * @param certificate
	 * @return
	 */
	boolean registerMedicalCertificateStatistics(final MedicalCertificate certificate);
}
