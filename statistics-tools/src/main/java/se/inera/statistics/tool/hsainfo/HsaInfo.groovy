package se.inera.statistics.tool.hsainfo

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
class HsaInfo {
    def static existerar = [:]
    def static types = [:]
    static void main(String[] args) {
        def props = new Properties()
        props.load(HsaInfo.class.getClassLoader().getResourceAsStream("dataSource.properties"))
        def config = new ConfigSlurper().parse(props)
        def sql = Sql.newInstance(config.datasource.url, config.dataSource.username, config.dataSource.password, config.dataSource.driver)
        int count = 0
        sql.eachRow('select h.DATA from HSA h') {row ->
//            println "${count++} ${row.DATA}: "
            count++
            Map hsa = new JsonSlurper().parseText(row.DATA)
            HsaInfo.count(hsa.enhet, "enhet")
            HsaInfo.count2(hsa.enhet?.agarform, "enhet.agarform")
            HsaInfo.count2(hsa.enhet?.enhetsTyp, "enhet.typ")
            HsaInfo.count2(hsa.enhet?.verksamhet, "enhet.verksamhet")
            HsaInfo.count2(hsa.enhet?.vardform, "enhet.vardform")
            HsaInfo.count2(hsa.enhet?.kommun, "enhet.kommun")
            HsaInfo.count2(hsa.enhet?.lan, "enhet.lan")
            HsaInfo.count(hsa.vardgivare, "vardgivare")
            HsaInfo.count2(hsa.vardgivare?.orgnr, "vardgivare.orgnr")
            HsaInfo.count(hsa.personal, "personal")
            HsaInfo.count2(hsa.personal?.kon, "personal.kon")
            HsaInfo.count2(hsa.personal?.alder, "personal.alder")
            HsaInfo.count2(hsa.personal?.befattning, "personal.befattning")
            HsaInfo.count2(hsa.personal?.specialitet, "personal.specialitet")
            HsaInfo.count2(hsa.personal?.yrkesgrupp, "personal.yrkesgrupp")
        }
        println("Totalt antal poster ${count}")
        println("Antal poster:")
        existerar.sort().each { println "${it.key}: ${it.value}" }
        println("Värdemängder:")
        types.each {
            print "${it.key}: "
            it.value.sort().each { print "'${it}', " }
            println ""
        }
    }

    static def count2(value, name) {
        count(value, name)
        if (value) {
            if (types[name]) {
                types[name] += value
            } else {
                types[name] = [] as Set
                types[name] += value;
            }
        }
    }

    static def count(value, name) {
        if (value) {
            if (existerar[name]) {
                existerar[name]++
            } else {
                existerar[name] = 1;
            }
        }
    }
}