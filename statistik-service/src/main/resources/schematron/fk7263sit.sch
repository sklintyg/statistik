<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://purl.oclc.org/dsdl/schematron"
    queryBinding='xslt2' schemaVersion='ISO19757-3'>

  <iso:title>Schematron file for "FK7263SIT".</iso:title>

  <iso:ns prefix="rg" uri="urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2"/>
  <iso:ns prefix="gn" uri="urn:riv:clinicalprocess:healthcond:certificate:2"/>
  <iso:ns prefix="tp" uri="urn:riv:clinicalprocess:healthcond:certificate:types:2"/>

  <iso:pattern id="intyg">
    <iso:rule context="//rg:intyg/gn:patient/gn:person-id"> 
      <iso:assert test="matches(normalize-space(tp:extension), '(.*[0-9]{6}.?[0-9]{4}.*)')">
        Den minsta gemensamma nämnaren för alla personnummer är att de först har minst 6 siffror i följd för att efter det ha 4 siffror i följd.
      </iso:assert>
    </iso:rule>

    <iso:rule context="//rg:intyg/gn:skapadAv/gn:enhet/gn:enhets-id/tp:extension">
      <iso:assert test="string-length(normalize-space(text())) > 0">Sträng kan inte vara tom.</iso:assert>
    </iso:rule>

    <iso:rule context="//rg:intyg">
      <iso:assert test="upper-case(gn:typ/tp:code) = 'FK7263'">Intygstypen måste vara FK7263</iso:assert>
      <iso:assert test="count(gn:svar[@id='32']) ge 1 and count(gn:svar[@id='32']) le 4">
        Ett 'MU' måste ha mellan 1 och 4 'Behov av sjukskrivning'
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32">
    <iso:rule context="//gn:svar[@id='32']">
      <iso:assert test="count(gn:delsvar[@id='32.1']) = 1">'Sjukskrivning' måste ha en 'grad'.</iso:assert>
      <iso:assert test="count(gn:delsvar[@id='32.2']) = 1">'Sjukskrivning' måste ha en 'period'.</iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32.1">
    <iso:rule context="//gn:delsvar[@id='32.1']">
      <iso:assert test="count(tp:cv) = 1">Ett värde av typen CV måste ha ett cv-element</iso:assert>
      <iso:assert test="count(tp:cv/tp:codeSystem) = 1">codeSystem är obligatoriskt</iso:assert>
      <iso:assert test="count(tp:cv/tp:code) = 1">code är obligatoriskt</iso:assert>
      <iso:assert test="count(tp:cv/tp:displayName) le 1">högst ett displayName kan anges</iso:assert>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0003'">'codeSystem' måste vara 'KV_FKMU_0003'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^[1-4]$')">
        'Sjukskrivningsnivå' kan ha ett av värdena 1, 2, 3 eller 4.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32.2">
    <iso:rule context="//gn:delsvar[@id='32.2']">
      <iso:assert test="tp:datePeriod">En period måste inneslutas av ett 'datePeriod'-element</iso:assert>
      <iso:assert test="tp:datePeriod/tp:start castable as xs:date">'from' måste vara ett giltigt datum.</iso:assert>
      <iso:assert test="tp:datePeriod/tp:end castable as xs:date">'tom' måste vara ett giltigt datum.</iso:assert>
    </iso:rule>
  </iso:pattern>

</iso:schema>
