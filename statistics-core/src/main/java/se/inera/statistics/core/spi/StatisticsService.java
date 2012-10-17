package se.inera.statistics.core.spi;

import se.inera.commons.support.ServiceResult;
import se.inera.statistics.core.api.MedicalCertificateDto;
import se.inera.statistics.core.api.StatisticsResult;

public interface StatisticsService {
	
	/**
	 * Load certificates based on search criterion
	 * @param search
	 * @return
	 */
	ServiceResult<StatisticsResult> loadByAge(String from, String to, String disability, String group);

	ServiceResult<StatisticsResult> loadStatisticsByDuration(String from, String to, String disability, String group);

	ServiceResult<StatisticsResult> loadStatisticsByMonth(String from, String to, String disability, String group);

	ServiceResult<StatisticsResult> loadStatisticsBySicknessGroups(final MedicalCertificateDto search);

	ServiceResult<StatisticsResult> loadStatisticsByCareUnit(final MedicalCertificateDto search);
}
