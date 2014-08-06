package se.inera.statistics.tool.hsainfo

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
class HsaInfo {
    def static existerar = [:]
    def static types = { [:].withDefault{ owner.call() } }()

    static void main(String[] args) {
        def props = new Properties()
        props.load(HsaInfo.class.getClassLoader().getResourceAsStream("dataSource.properties"))
        def config = new ConfigSlurper().parse(props)
        def sql = Sql.newInstance(config.datasource.url, config.dataSource.username, config.dataSource.password, config.dataSource.driver)
        int count = 0
        sql.eachRow('select h.DATA from HSA h') {row ->
            count++

            Map hsa = new JsonSlurper().parseText(row.DATA)
            HsaInfo.count(hsa.enhet, "enhet")
            HsaInfo.count(hsa.enhet?.agarform, "enhet.agarform")
            HsaInfo.count(hsa.enhet?.enhetsTyp, "enhet.typ")
            HsaInfo.count(hsa.enhet?.verksamhet, "enhet.verksamhet")
            HsaInfo.count(hsa.enhet?.vardform, "enhet.vardform")
            HsaInfo.count(hsa.enhet?.kommun, "enhet.kommun")
            HsaInfo.count(hsa.enhet?.lan, "enhet.lan")
            HsaInfo.count(hsa.vardgivare, "vardgivare")
            HsaInfo.count(hsa.vardgivare?.orgnr, "vardgivare.orgnr")
            HsaInfo.count(hsa.personal, "personal")
            HsaInfo.count(hsa.personal?.kon, "personal.kon")
            HsaInfo.count(hsa.personal?.alder, "personal.alder")
            HsaInfo.count(hsa.personal?.befattning, "personal.befattning")
            HsaInfo.count(hsa.personal?.specialitet, "personal.specialitet")
            HsaInfo.count(hsa.personal?.yrkesgrupp, "personal.yrkesgrupp")
        }

        println("Totalt antal poster ${count}")
        println("Antal poster:")
        existerar.sort().each { println "${it.key}: ${it.value}" }
        println("Värdemängder:")
        types.each {
            print "${it.key}: "
            it.value.sort().each { print "'${it.key}'(${it.value}), " }
            println ""
        }
    }

    static def count(value, name) {
        if (value) {
            if (existerar[name]) {
                existerar[name]++
            } else {
                existerar[name] = 1;
            }
            if (value.getClass() == ArrayList) {
                if (types."$name:histogram"."${value.size}") {
                    types."$name:histogram"."${value.size}"++
                } else {
                    types."$name:histogram"."${value.size}" = 1
                }
                value.each {
                    if (types."$name"."$it") {
                        types."$name"."$it"++
                    } else {
                        types."$name"."$it" = 1
                    }
                }
            }
        }
    }
}