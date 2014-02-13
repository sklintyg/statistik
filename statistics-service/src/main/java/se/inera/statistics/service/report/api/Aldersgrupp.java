/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.statistics.service.report.api;

import org.joda.time.LocalDate;

import se.inera.statistics.service.report.model.Kon;
import se.inera.statistics.service.report.model.SimpleDualSexDataRow;
import se.inera.statistics.service.report.model.SimpleDualSexResponse;
import se.inera.statistics.service.report.repository.RollingLength;
import se.inera.statistics.service.report.util.Verksamhet;

public interface Aldersgrupp {

    void count(String period, String hsaId, String group, RollingLength rollingLength, Verksamhet typ, Kon kon);

    SimpleDualSexResponse<SimpleDualSexDataRow> getCurrentAgeGroups(String hsaId);

    SimpleDualSexResponse<SimpleDualSexDataRow> getHistoricalAgeGroups(String hsaId, LocalDate when, RollingLength rollingLength);
}
