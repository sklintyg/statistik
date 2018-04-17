/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import groovy.util.CliBuilder
import org.apache.commons.cli.Option

class UpdateEnhetNamnFromHsaFileService {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateEnhetNamnFromHsaFileService.class);

    static void main(String[] args) {
        def cli = new CliBuilder(usage:'fileservice [options]',
                header:'Options:', stopAtNonOption:false)
        cli.cp(longOpt:"classpath", 'does nothing')
        cli.f(longOpt:"hsafileservice", args:1, argName:'file', 'path to hsa fileservice properties (use this or "-u" option)')
        cli.d(longOpt:"datasource", args:1, argName:'file', 'path to hsa datasource properties')
        cli.u(longOpt:"units", args:1, argName:'file', 'path to hsa unit names xml file (use this or "-f" option)')
        def options = cli.parse(args)

        long start = System.currentTimeMillis()
        InputStream unitStream;
        if (options.u) {
            unitStream = new FileInputStream(options.u)
        } else {
            def hsaProps = new Properties();
            String hsaPath = options.f ? options.f : System.properties.getProperty("hsafileservice", "hsaFileService.properties")
            new File(hsaPath).withInputStream { stream ->
                hsaProps.load(stream)
            }
            def hsaConf = new ConfigSlurper().parse(hsaProps)
            unitStream = HsaUnitSource.getUnits(hsaConf.certificate.file, hsaConf.certificate.password, hsaConf.truststore.file, hsaConf.truststore.password, hsaConf.hsaunits.url)
        }
        def enhetsXml = new XmlSlurper().parse(unitStream)

        def dbProps = new Properties()
        String dbPath = options.d ? options.d : System.properties.getProperty("datasource", "dataSource.properties")
        new File(dbPath).withInputStream { stream ->
            dbProps.load(stream)
        }
        def dbConf = new ConfigSlurper().parse(dbProps)
        BasicDataSource dataSource =
            new BasicDataSource(driverClassName: dbConf.dataSource.driver, url: dbConf.dataSource.url,
                                username: dbConf.dataSource.username, password: dbConf.dataSource.password,
                                initialSize: 1, maxTotal: 1)


        Sql sql = new Sql(dataSource)
        def count = 0
        def totalEnheter = 0
        enhetsXml.hsaUnits.hsaUnit.each {
            def id = it.hsaIdentity.text()
            def namn = it.name.text()
            def updated = sql.executeUpdate('update enhet set namn = :namn where enhetId = :id and namn <> :namn' , [namn: namn, id : id])
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
