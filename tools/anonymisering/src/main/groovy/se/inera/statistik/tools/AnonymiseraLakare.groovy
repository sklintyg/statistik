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
import org.apache.commons.dbcp2.BasicDataSource
import se.inera.statistik.tools.anonymisering.base.AnonymiseraHsaId
import se.inera.statistik.tools.anonymisering.base.AnonymizeString

import java.util.concurrent.atomic.AtomicInteger

class AnonymiseraLakare {

    private BasicDataSource dataSource;

    AnonymiseraLakare(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    void anonymize(AnonymiseraHsaId anonymiseraHsaId) {
        final AtomicInteger count = new AtomicInteger(0)
        final AtomicInteger errorCount = new AtomicInteger(0)

        println "Start anonymization of lakare"
        long start = System.currentTimeMillis()

        def query = '''
        SELECT id 
        FROM lakare
        '''

        def sql = new Sql(dataSource)
        def hsaIds = sql.rows(query)
        println "${hsaIds.size()} lakare found to anonymize"
        sql.close()

        def output = hsaIds.collect {
            StringBuffer result = new StringBuffer()
            sql = new Sql(dataSource)

            def id = it.id
            try {
                def lakare = sql.firstRow('SELECT lakareid, tilltalsnamn, efternamn FROM lakare WHERE id = :id', [id: id])
                def lakareid = anonymiseraHsaId.anonymisera(lakare.lakareid)
                def tilltalsnamn = AnonymizeString.anonymize(lakare.tilltalsnamn)
                def efternamn = AnonymizeString.anonymize(lakare.efternamn)

                sql.executeUpdate('UPDATE lakare SET lakareid = :lakareid, tilltalsnamn = :tilltalsnamn, efternamn = :efternamn WHERE id = :id',
                        [lakareid: lakareid, tilltalsnamn: tilltalsnamn, efternamn: efternamn, id: id])

                int current = count.addAndGet(1)
                if (current % 10000 == 0) {
                    println "${current} lakare anonymized in ${(int) ((System.currentTimeMillis() - start) / 1000)} seconds"
                }
            } catch (Throwable t) {
                t.printStackTrace()

                result << "Anonymization of lakare ${id} failed: ${t}"
                errorCount.incrementAndGet()
            } finally {
                sql.close()
            }
            result.toString()
        }

        long end = System.currentTimeMillis()
        output.each { line ->
            if (line) println line
        }

        println "Done! ${count} lakare anonymized with ${errorCount} errors in ${(int) ((end - start) / 1000)} seconds"
    }

}



