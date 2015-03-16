/**
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistics.gatling

import io.gatling.core.Predef._

object Filter {
   def getFilterData(filterHash: String) = RestCall.get(
     s"filter: hash = $filterHash",
     s"${Conf.uri}/api/filter/$filterHash")

  def getFilterHash(filterData: String) = RestCall.get(
    s"filter: hash = $filterData",
    s"${Conf.uri}/api/filter/$filterData")

}