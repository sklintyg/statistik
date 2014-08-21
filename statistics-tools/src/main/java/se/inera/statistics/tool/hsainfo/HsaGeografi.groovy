package se.inera.statistics.tool.hsainfo

import groovy.json.JsonSlurper
import groovy.sql.Sql

class HsaGeografi {
    def static existerar = [:]

    static void main(String[] args) {
        def props = new Properties()
        props.load(HsaGeografi.class.getClassLoader().getResourceAsStream("dataSource.properties"))
        def config = new ConfigSlurper().parse(props)
        def sql = Sql.newInstance(config.datasource.url, config.dataSource.username, config.dataSource.password, config.dataSource.driver)
        int count = 0
        sql.eachRow('select h.DATA from HSA h') { row ->
            count++

            Map hsa = new JsonSlurper().parseText(row.DATA)

            def enhet = hsa?.huvudenhet ?: hsa?.enhet
            def enhetid = enhet?.id

            def lan = hsa?.enhet?.geografi?.lan ?: hsa?.huvudenhet?.geografi?.lan
            def kommun = hsa?.enhet?.geografi?.kommun ?: hsa?.huvudenhet?.geografi?.kommun

//            HsaGeografi.count(true, "vårdgivare: ${hsa?.vardgivare?.id ?: null} enhet ${enhetid} län: ${lan} kommun: ${kommun}")
            HsaGeografi.count(true, "${hsa?.vardgivare?.id ?: null};${enhetid};${lan};${kommun}")
        }

        println("Totalt antal poster ${count}")
        println("Antal poster: ${existerar.size()}")
        println("vårdgivare;enhet;län;kommun;totalt")
        existerar.sort().each { println "${it.key};${it.value}" }
    }

    static def count(value, name) {
        def key = String.valueOf(name);
        if (value) {
            if (existerar[key]) {
                existerar[key]++
            } else {
                existerar[key] = 1;
            }
        }
    }
}