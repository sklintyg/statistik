/**
 * Copyright (C) 2012 Inera AB (http://www.inera.se)
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
package se.inera.statistics.core.spi;

import se.inera.statistics.core.api.MedicalCertificateDto;

public interface RegisterStatisticsService {

	/**
	 * Add a medical certificate statistic data to the storage
	 * @param certificate
	 * @return
	 */
	boolean registerMedicalCertificateStatistics(final MedicalCertificateDto certificate);
}
