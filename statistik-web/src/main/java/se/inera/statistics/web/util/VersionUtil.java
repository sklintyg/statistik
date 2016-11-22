/**
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.statistics.web.util;

import org.springframework.beans.factory.annotation.Value;

public class VersionUtil {

    @Value("${statistik.project.version}")
    private String projectVersion;

    @Value("${statistik.project.buildtime}")
    private String buildTime;

    @Value("${statistik.project.buildhost}")
    private String buildHost;

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public String getBuildHost() {
        return buildHost;
    }
}
