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
package se.inera.statistik.tools

import groovy.sql.Sql

import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UpdateEnhetNamnFromHsaFileService {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateEnhetNamnFromHsaFileService.class);

    static void main(String[] args) {
        long start = System.currentTimeMillis()
        def hsaProps = new Properties();
        new File("hsaFileService.properties").withInputStream { stream ->
            hsaProps.load(stream)
        }
        def hsaConfig = new ConfigSlurper().parse(hsaProps)

        def props = new Properties()
        new File("dataSource.properties").withInputStream { stream ->
            props.load(stream)
        }
        def config = new ConfigSlurper().parse(props)
        BasicDataSource dataSource =
            new BasicDataSource(driverClassName: config.dataSource.driver, url: config.dataSource.url,
                                username: config.dataSource.username, password: config.dataSource.password,
                                initialSize: 1, maxTotal: 1)

        InputStream unitStream = HsaUnitSource.getUnits(hsaConfig.certificate.file, hsaConfig.certificate.password, hsaConfig.truststore.file, hsaConfig.truststore.file, hsaConfig.hsaunits.url)
        def enhetsXml = new XmlSlurper().parse(unitStream)

        Sql sql = new Sql(dataSource)
        def count = 0
        def totalEnheter = 0
        println(enhetsXml)
        println(enhetsXml.hsaUnits)
        enhetsXml.hsaUnits.hsaUnit.each {
            def id = it.hsaIdentity.text()
            def namn = it.name.text()
            def updated = sql.executeUpdate('update enhet set namn = :namn where enhetId = :id' , [namn: namn, id : id])
            if (updated > 0) {
                count += updated
                LOG.info("Id: $id, Namn: $namn")
            }
            totalEnheter ++
        }
        long end = System.currentTimeMillis()
        LOG.info("$count enheter uppdated of $totalEnheter enheter in ${end-start} milliseconds")
    }

}
