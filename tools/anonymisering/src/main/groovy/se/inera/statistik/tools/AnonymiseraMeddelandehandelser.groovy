/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.statistik.tools

import groovy.sql.Sql
import groovyx.gpars.GParsPool
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraXml

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraMeddelandehandelser {

    private final int DEFAULT_NUMBEROFTHREADS = 5;

    private BasicDataSource dataSource;

    AnonymiseraMeddelandehandelser(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void anonymize(int numberOfThreads, AnonymiseraXml anonymiseraXml) {
        numberOfThreads = numberOfThreads > 0 ? numberOfThreads : DEFAULT_NUMBEROFTHREADS

        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)

        println "Start anonymization of meddelanden"
        long start = System.currentTimeMillis()

        def query = '''
        SELECT id 
        FROM meddelandehandelse
        '''

        def bootStrapsql = new Sql(dataSource)
        def identities = bootStrapsql.rows(query)
        println "${identities.size()} meddelanden found"
        bootStrapsql.close()

        def output
        GParsPool.withPool(numberOfThreads) {
            output = identities.collect {
                StringBuffer result = new StringBuffer()
                def sql = new Sql(dataSource)
                def identity = it.id
                try {
                    def meddelande = sql.firstRow("SELECT type, data FROM meddelandehandelse WHERE id = :id", [id: identity])

                    try {
                        String xmlDoc = meddelande?.data
                        String anonymiseradXml = xmlDoc ? anonymiseraXml.anonymiseraMeddelandeXml(xmlDoc, meddelande?.type) : null
                        if (anonymiseradXml) {
                            sql.executeUpdate("UPDATE meddelandehandelse SET data = :document WHERE id = :id",
                                    [document: anonymiseradXml, id: identity])
                        }
                    } catch (Exception ignore) {
                        println "Failed to parse meddelande ${identity}"
                    }

                    int current = count.addAndGet(1)
                    if (current % 10000 == 0) {
                        println "${current} meddelanden anonymized in ${(int) ((System.currentTimeMillis() - start) / 1000)} seconds"
                    }
                } catch (Throwable t) {
                    t.printStackTrace()

                    result << "Anonymization of meddelande ${identity} failed: ${t}"
                    errorCount.incrementAndGet()
                } finally {
                    sql.close()
                }
                result.toString()
            }
        }

        long end = System.currentTimeMillis()
        output.each { line ->
            if (line) println line
        }

        println "Done! ${count} meddelanden anonymized with ${errorCount} errors in ${(int) ((end - start) / 1000)} seconds"
    }

}



