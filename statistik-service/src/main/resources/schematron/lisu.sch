<?xml version="1.0" encoding="utf-8"?>
<iso:schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://purl.oclc.org/dsdl/schematron"
    queryBinding='xslt2' schemaVersion='ISO19757-3'>

  <iso:title>Schematron file for "Läkarintyg för sjukpenning utökat".</iso:title>

  <iso:ns prefix="xs" uri="http://www.w3.org/2001/XMLSchema"/>
  <iso:ns prefix="rg" uri="urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:2"/>
  <iso:ns prefix="gn" uri="urn:riv:clinicalprocess:healthcond:certificate:2"/>
  <iso:ns prefix="tp" uri="urn:riv:clinicalprocess:healthcond:certificate:types:2"/>

  <iso:pattern id="intyg">
    <iso:rule context="//rg:intyg">
      <iso:assert test="count(gn:svar[@id='1']) ge 1 and count(gn:svar[@id='1']) le 4">
        Ett 'MU' måste ha mellan 1 och 4 'Grund för medicinskt underlag'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='28']) = 1">
        Ett 'MU' måste ha ett 'Typ av sysselsättning'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='29']) le 1">
        Ett 'MU' får ha högst ett 'Nuvarande arbete'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='30']) le 1">
        Ett 'MU' får ha högst ett 'Arbetsmarknadspolitiskt program'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='6']) ge 1 and count(gn:svar[@id='6']) le 3">
        Ett 'MU' måste ha mellan 1 och 3 'Typ av diagnos'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='35']) = 1">
        Ett 'MU' måste ha ett 'Funktionsnedsättning'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='17']) = 1">
        Ett 'MU' måste ha ett 'Aktivitetsbegränsningar'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='19']) le 1">
        Ett 'MU' får ha högst ett 'Pågående medicinska behandlingar'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='20']) le 1">
        Ett 'MU' får ha högst ett 'Planerade medicinska behandlingar'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='32']) ge 1 and count(gn:svar[@id='32']) le 4">
        Ett 'MU' måste ha mellan 1 och 4 'Behov av sjukskrivning'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='37']) le 1">
        Ett 'MU' får ha högst ett 'Försäkringsmedicinskt beslutsstöd'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='33']) le 1">
        Ett 'MU' får ha högst ett 'Arbetstidsförläggning'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='34']) le 1">
        Ett 'MU' får ha högst ett 'Arbetsresor'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='23']) le 1">
        Ett 'MU' får ha högst ett 'Förmåga trots begränsningar'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='39']) = 1">
        Ett 'MU' måste ha ett 'Prognos'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='40']) = 1">
        Ett 'MU' måste ha ett 'Arbetslivsinriktade åtgärder'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='25']) le 1">
        Ett 'MU' får ha högst ett 'Övrigt'
      </iso:assert>
      <iso:assert test="count(gn:svar[@id='26']) le 1">
        Ett 'MU' får ha högst ett 'Kontakt önskas'
      </iso:assert>
      <iso:let name="svarsIdExpr" value="'^([16]|1[79]|2[035689]|3[0234579]|40|9[0-9]{3})$'"/>
      <iso:assert test="count(gn:svar[not(matches(@id, $svarsIdExpr))]) = 0">
        Oväntat svars-id. Svars-id:n måste matcha "<value-of select="$svarsIdExpr"/>".
      </iso:assert>
      <!-- Meddelandet kan innehålla ett valfritt antal tilläggsfrågor -->
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q1">
    <iso:rule context="//gn:svar[@id='1']">
      <iso:assert test="count(gn:instans) = 1">
        'Grund för medicinskt underlag (MU)' måste ha ett instansnummer.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='1.1']) = 1">
        'Grund för medicinskt underlag (MU)' måste ha ett 'Typ av grund för MU'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='1.2']) = 1">
        'Grund för medicinskt underlag (MU)' måste ha ett 'Datum som grund för MU'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='1.3']) le 1">
        'Grund för medicinskt underlag (MU)' får ha högst ett 'Vilken annan grund finns för MU'.
      </iso:assert>
      <iso:assert test="not(preceding-sibling::gn:svar[@id='1']/gn:delsvar[@id='1.1']/tp:cv/tp:code/normalize-space() = normalize-space(gn:delsvar[@id='1.1']/tp:cv/tp:code))">
        Samma 'Typ av grund för MU' kan inte användas flera gånger i samma 'MU'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^1\.[123]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q1.1">
    <iso:rule context="//gn:delsvar[@id='1.1']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0001'">'codeSystem' måste vara 'KV_FKMU_0001'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(UNDERSOKNING|TELEFONKONTAKT|JOURNALUPPGIFTER|ANNAT)$')">
        'Typ av grund för MU' kan ha ett av värdena UNDERSOKNING, TELEFONKONTAKT, JOURNALUPPGIFTER, ANNAT.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q1.2">
    <iso:rule context="//gn:delsvar[@id='1.2']">
      <iso:extends rule="date"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q1.3">
    <iso:rule context="//gn:delsvar[@id='1.3']">
      <iso:extends rule="non-empty-string"/>
      <iso:assert test="count(../gn:delsvar[@id='1.1']/tp:cv/tp:code[normalize-space(.) != 'ANNAT']) = 0">
        Om 'Typ av grund för MU' inte är 'Annat' så får 'Vilken annan grund finns för MU' inte anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q1.1-1.3">
    <iso:rule context="//gn:delsvar[@id='1.1']/tp:cv/tp:code[normalize-space(.) = 'ANNAT']">
      <iso:assert test="../../../gn:delsvar[@id='1.3']">
        Om 'Typ av grund för MU' är 'Annat' så måste 'Vilken annan grund finns för MU' anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q28">
    <iso:rule context="//gn:svar[@id='28']">
      <iso:assert test="count(gn:delsvar[@id='28.1']) = 1">
        'Typ av sysselsättning' måste ha ett 'Typ av sysselsättning'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^28\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q28.1">
    <iso:rule context="//gn:delsvar[@id='28.1']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0002'">'codeSystem' måste vara 'KV_FKMU_0002'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(NUVARANDE_ARBETE|ARBETSSOKANDE|FORALDRALEDIG|STUDIER|PROGRAM)$')">
        'Typ av sysselsättning' kan ha ett av värdena NUVARANDE_ARBETE, ARBETSSOKANDE, FORALDRALEDIG, STUDIER eller PROGRAM.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q28.1-q29">
    <iso:rule context="//gn:delsvar[@id='28.1']/tp:cv/tp:code[normalize-space(.) = 'NUVARANDE_ARBETE']">
      <iso:assert test="count(../../../../gn:svar[@id='29']) = 1">
        Om 'Typ av sysselsättning' besvarats med 1, måste 'Nuvarande arbete' besvaras
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='28.1']/tp:cv/tp:code[normalize-space(.) != 'NUVARANDE_ARBETE']">
      <iso:assert test="count(../../../../gn:svar[@id='29']) = 0">
        Om 'Typ av sysselsättning' besvarats med något annat än 1, får 'Nuvarande arbete' inte besvaras
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q28.1-q30">
    <iso:rule context="//gn:delsvar[@id='28.1']/tp:cv/tp:code[normalize-space(.) = 'PROGRAM']">
      <iso:assert test="count(../../../../gn:svar[@id='30']) = 1">
        Om 'Typ av sysselsättning' besvarats med 5, måste 'Arbetsmarknadspolitiskt program' besvaras
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='28.1']/tp:cv/tp:code[normalize-space(.) != 'PROGRAM']">
      <iso:assert test="count(../../../../gn:svar[@id='30']) = 0">
        Om 'Typ av sysselsättning' besvarats med något annat än 5, får 'Arbetsmarknadspolitiskt program' inte besvaras
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q29">
    <iso:rule context="//gn:svar[@id='29']">
      <iso:assert test="count(gn:delsvar[@id='29.1']) = 1">
        'Nuvarande arbete' måste ha ett 'Yrke och arbetsuppgifter'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^29\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q29.1">
    <iso:rule context="//gn:delsvar[@id='29.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q30">
    <iso:rule context="//gn:svar[@id='30']">
      <iso:assert test="count(gn:delsvar[@id='30.1']) = 1">
        'Arbetsmarknadspolitiskt program' måste ha ett 'Aktiviteter i arbetsmarknadspolitiskt program'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^30\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q30.1">
    <iso:rule context="//gn:delsvar[@id='30.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q6">
    <iso:rule context="//gn:svar[@id='6']">
      <iso:assert test="count(gn:instans) = 1">
        'Typ av diagnos' måste ha ett instansnummer.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='6.1']) = 1">
        'Typ av diagnos' måste ha en 'Diagnostext'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='6.2']) = 1">
        'Typ av diagnos' måste ha en 'Diagnoskod ICD-10'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^6\.[12]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q6.1">
    <iso:rule context="//gn:delsvar[@id='6.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q6.2">
    <iso:rule context="//gn:delsvar[@id='6.2']"> 
      <iso:extends rule="cv"/>
      <iso:assert test="matches(normalize-space(tp:cv/tp:codeSystem), '(1.2.752.116.1.1.1.1.3|1.2.752.116.1.3.1.1.2)')">
        Diagnoskodsystem måste vara OID för ICD-10-SE eller KSH97-P.
      </iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code),'^([A-EG-Ya-eg-y][0-9]{2}[A-Za-z0-9-]*|[Zz][0-689][0-9][A-Za-z0-9-]*|[Zz]7[0-24-9][A-Za-z0-9-]*|[Zz]73[A-Za-z0-9-]+|[Ff][0-9]{2}[A-Za-z0-9-]+)$')">
        Diagnoskod måste anges som bokstav följt av två siffror följt av noll eller flera bokstäver, siffror eller bindestreck, d.v.s. minst
        tre positioner måste anges. Om diagnoskoden börjar med F eller Z73 måste bokstav och två siffor följas av minst en bokstav, siffra
        eller bindestreck, d.v.s. minst fyra positioner måste anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q35">
    <iso:rule context="//gn:svar[@id='35']">
      <iso:assert test="count(gn:delsvar[@id='35.1']) = 1">
        'Funktionsnedsättning' måste ha ett 'Beskrivning'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^35\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q35.1">
    <iso:rule context="//gn:delsvar[@id='35.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q17">
    <iso:rule context="//gn:svar[@id='17']">
      <iso:assert test="count(gn:delsvar[@id='17.1']) = 1">
        'Aktivitetsbegränsningar' måste ha ett 'Beskrivning'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^17\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q17.1">
    <iso:rule context="//gn:delsvar[@id='17.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q19">
    <iso:rule context="//gn:svar[@id='19']">
      <iso:assert test="count(gn:delsvar[@id='19.1']) = 1">
        'Pågående medicinska behandlingar' måste ha ett 'Typ av pågående behandlingar och ansvarig vårdenhet'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^19\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q19.1">
    <iso:rule context="//gn:delsvar[@id='19.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q20">
    <iso:rule context="//gn:svar[@id='20']">
      <iso:assert test="count(gn:delsvar[@id='20.1']) = 1">
        'Planerade medicinska behandlingar' måste ha ett 'Typ av planerade behandlingar och ansvarig vårdenhet'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^20\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q20.1">
    <iso:rule context="//gn:delsvar[@id='20.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32">
    <iso:rule context="//gn:svar[@id='32']">
      <iso:assert test="count(gn:instans) = 1">
        'Sjukskrivning' måste ha ett instansnummer.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='32.1']) = 1">'Sjukskrivning' måste ha en 'grad'.</iso:assert>
      <iso:assert test="count(gn:delsvar[@id='32.2']) = 1">'Sjukskrivning' måste ha en 'period'.</iso:assert>
      <iso:let name="cstart" value="normalize-space(gn:delsvar[@id='32.2']/tp:datePeriod/tp:start)"/>
      <iso:let name="cend" value="normalize-space(gn:delsvar[@id='32.2']/tp:datePeriod/tp:end)"/>
      <iso:assert test="not(preceding-sibling::gn:svar[@id='32']/gn:delsvar[@id='32.2']/tp:datePeriod/tp:start[normalize-space(.) lt $cend and normalize-space(../tp:end) gt $cstart])">
        Två 'perioder' kan inte vara överlappande.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^32\.[12]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32.1">
    <iso:rule context="//gn:delsvar[@id='32.1']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0003'">'codeSystem' måste vara 'KV_FKMU_0003'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(HELT_NEDSATT|TRE_FJARDEDEL|HALFTEN|EN_FJARDEDEL)$')">
        'Sjukskrivningsnivå' kan ha ett av värdena HELT_NEDSATT, TRE_FJARDEDEL, HALFTEN, EN_FJARDEDEL.
      </iso:assert>
    </iso:rule>
  </iso:pattern>
›
  <iso:pattern id="q32.2">
    <iso:rule context="//gn:delsvar[@id='32.2']">
      <iso:extends rule="period"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q32.1-33">
    <iso:rule context="//gn:delsvar[@id='32.1']/tp:cv/tp:code[matches(normalize-space(.), '^(TRE_FJARDEDEL|HALFTEN|EN_FJARDEDEL)$')]">
      <iso:assert test="count(../../../../gn:svar[@id='33']) = 1">
        Om 'Sjukskrivningsnivå' är TRE_FJARDEDEL, HALFTEN eller EN_FJARDEDEL så måste 'Arbetstidsförläggning' anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q37">
    <iso:rule context="//gn:svar[@id='37']">
      <iso:assert test="count(gn:delsvar[@id='37.1']) = 1">
        'Försäkringsmedicinskt beslutsstöd' måste ha ett 'Överskrider försäkringsmedicinskt beslutsstöd'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^37\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q37.1">
    <iso:rule context="//gn:delsvar[@id='37.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q33">
    <iso:rule context="//gn:svar[@id='33']">
      <iso:assert test="count(gn:delsvar[@id='33.1']) = 1">
        'Arbetstidsförläggning' måste ha ett 'Om arbetstidsförläggning'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='33.2']) le 1">
        'Arbetstidsförläggning' får ha högst ett 'Motivering arbetstidsförläggning'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^33\.[12]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q33.1">
    <iso:rule context="//gn:delsvar[@id='33.1']">
      <iso:extends rule="boolean"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q33.2">
    <iso:rule context="//gn:delsvar[@id='33.2']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q33.1-33.2">
    <iso:rule context="//gn:delsvar[@id='33.1' and (normalize-space(.) = '1' or normalize-space(.) = 'true')]">
      <iso:assert test="../gn:delsvar[@id='33.2']">
        Om 'Om arbetstidsförläggning' är 'true' så måste 'Motivering arbetstidsförläggning' anges.
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='33.1' and (normalize-space(.) = '0' or normalize-space(.) = 'false')]">
      <iso:assert test="count(../gn:delsvar[@id='33.2']) = 0">
        Om 'Om arbetstidsförläggning' är 'false' så får 'Motivering arbetstidsförläggning' inte anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q34">
    <iso:rule context="//gn:svar[@id='34']">
      <iso:assert test="count(gn:delsvar[@id='34.1']) = 1">
        'Arbetsresor' måste ha ett 'Om arbetsresor'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^34\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q34.1">
    <iso:rule context="//gn:delsvar[@id='34.1']">
      <iso:extends rule="boolean"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q23">
    <iso:rule context="//gn:svar[@id='23']">
      <iso:assert test="count(gn:delsvar[@id='23.1']) = 1">
        'Förmåga trots begränsningar' måste ha ett 'Beskrivning'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^23\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q23.1">
    <iso:rule context="//gn:delsvar[@id='23.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q39">
    <iso:rule context="//gn:svar[@id='39']">
      <iso:assert test="count(gn:delsvar[@id='39.1']) = 1">
        'Prognos' måste ha ett 'Beskrivning prognos'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='39.3']) le 1">
        'Prognos' får ha högst ett 'Patienten kommer med stor sannolikhet att återgå helt i nuvarande sysselsättning efter x antal dagar'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^39\.[13]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q39.1">
    <iso:rule context="//gn:delsvar[@id='39.1']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0006'">'codeSystem' måste vara 'KV_FKMU_0006'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(STOR_SANNOLIKHET|SANNOLIKT_INTE|PROGNOS_OKLAR|ATER_X_ANTAL_DGR)$')">
        'Beskrivning prognos' kan ha ett av värdena STOR_SANNOLIKHET, SANNOLIKT_INTE, PROGNOS_OKLAR eller ATER_X_ANTAL_DGR.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q39.3">
    <iso:rule context="//gn:delsvar[@id='39.3']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0007'">'codeSystem' måste vara 'KV_FKMU_0007'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(30_DAGAR|60_DAGAR|90_DAGAR|180_DAGAR)$')">
        'Patienten kommer med stor sannolikhet att återgå helt i nuvarande sysselsättning efter x antal dagar' kan ha ett av värdena 30_DAGAR, 60_DAGAR, 90_DAGAR eller 180_DAGAR.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q39.1-39.3">
    <iso:rule context="//gn:delsvar[@id='39.1']/tp:cv/tp:code[normalize-space(.) = 'ATER_X_ANTAL_DGR']">
      <iso:assert test="../../../gn:delsvar[@id='39.3']">
      Om 'Beskrivning prognos' är ATER_X_ANTAL_DGR så måste 'Patienten kommer med stor sannolikhet att återgå helt i nuvarande sysselsättning efter x antal dagar' anges.
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='39.1']/tp:cv/tp:code[normalize-space(.) != 'ATER_X_ANTAL_DGR']">
      <iso:assert test="count(../../../gn:delsvar[@id='39.3']) = 0">
        Om 'Beskrivning prognos' inte är ATER_X_ANTAL_DGR så får 'Patienten kommer med stor sannolikhet att återgå helt i nuvarande sysselsättning efter x antal dagar' inte anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40">
    <iso:rule context="//gn:svar[@id='40']">
      <iso:assert test="count(gn:delsvar[@id='40.1']) ge 1 and count(gn:delsvar[@id='40.1']) le 10">
        'Arbetslivsinriktade åtgärder' måste ha mellan 1 och 10 'Val av arbetslivsinriktade åtgärder'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='40.2']) le 1">
        'Arbetslivsinriktade åtgärder' får ha högst ett 'Beskrivning arbetslivsriktade åtgärder aktuellt'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='40.3']) le 1">
        'Arbetslivsinriktade åtgärder' får ha högst ett 'Beskrivning arbetslivsriktade åtgärder ej aktuellt'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^40\.[123]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40.1">
    <iso:rule context="//gn:delsvar[@id='40.1']">
      <iso:extends rule="cv"/>
      <iso:assert test="tp:cv/tp:codeSystem = 'KV_FKMU_0004'">'codeSystem' måste vara 'KV_FKMU_0004'.</iso:assert>
      <iso:assert test="matches(normalize-space(tp:cv/tp:code), '^(EJ_AKTUELLT|ARBETSTRANING|ARBETSANPASSNING|SOKA_NYTT_ARBETE|BESOK_ARBETSPLATS|ERGONOMISK|HJALPMEDEL|KONFLIKTHANTERING|KONTAKT_FHV|OMFORDELNING|OVRIGA_ATGARDER)$')">
        'Val av arbetslivsinriktade åtgärder' kan ha ett av värdena EJ_AKTUELLT, ARBETSTRANING, ARBETSANPASSNING, SOKA_NYTT_ARBETE, BESOK_ARBETSPLATS, ERGONOMISK, HJALPMEDEL, KONFLIKTHANTERING, KONTAKT_FHV, OMFORDELNING eller OVRIGA_ATGARDER.
      </iso:assert>
      <iso:assert test="not(preceding-sibling::gn:delsvar[@id='40.1']/tp:cv/tp:code[.!='EJ_AKTUELLT'] and tp:cv/tp:code[.='EJ_AKTUELLT'])">
        'Inte aktuellt' kan inte kombineras med andra svar
      </iso:assert>
      <iso:assert test="not(preceding-sibling::gn:delsvar[@id='40.1']/tp:cv/tp:code[.='EJ_AKTUELLT'] and tp:cv/tp:code[.!='EJ_AKTUELLT'])">
        'Inte aktuellt' kan inte kombineras med andra svar
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40.2">
    <iso:rule context="//gn:delsvar[@id='40.2']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40.3">
    <iso:rule context="//gn:delsvar[@id='40.3']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40.1-40.2">
    <iso:rule context="//gn:delsvar[@id='40.1']/tp:cv/tp:code[normalize-space(.) != 'EJ_AKTUELLT']">
      <iso:assert test="../../../gn:delsvar[@id='40.2']">
        Om 'Val av arbetslivsinriktade åtgärder' är skiljt från 'EJ_AKTUELLT' så måste 'Beskrivning arbetslivsriktade åtgärder aktuellt' anges.
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='40.1']/tp:cv/tp:code[normalize-space(.) = 'EJ_AKTUELLT']">
      <iso:assert test="count(../../../gn:delsvar[@id='40.2']) = 0">
        Om 'Val av arbetslivsinriktade åtgärder' är 'EJ_AKTUELLT' så får 'Beskrivning arbetslivsriktade åtgärder aktuellt' inte anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q40.1-40.3">
    <iso:rule context="//gn:delsvar[@id='40.1']/tp:cv/tp:code[normalize-space(.) = 'EJ_AKTUELLT']">
      <iso:assert test="../../../gn:delsvar[@id='40.3']">
        Om 'Val av arbetslivsinriktade åtgärder' är 'EJ_AKTUELLT' så måste 'Beskrivning arbetslivsriktade åtgärder ej aktuellt' anges.
      </iso:assert>
    </iso:rule>
    <iso:rule context="//gn:delsvar[@id='40.1']/tp:cv/tp:code[normalize-space(.) != 'EJ_AKTUELLT']">
      <iso:assert test="count(../../../gn:delsvar[@id='40.3']) = 0">
        Om 'Val av arbetslivsinriktade åtgärder' är skiljt från 'EJ_AKTUELLT' så får 'Beskrivning arbetslivsriktade åtgärder ej aktuellt' inte anges.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q25">
    <iso:rule context="//gn:svar[@id='25']">
      <iso:assert test="count(gn:delsvar[@id='25.1']) = 1">
        'Övrigt' måste ha ett 'Typ av övriga upplysningar'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^25\.[1]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q25.1">
    <iso:rule context="//gn:delsvar[@id='25.1']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q26">
    <iso:rule context="//gn:svar[@id='26']">
      <iso:assert test="count(gn:delsvar[@id='26.1']) = 1">
        'Kontakt önskas' måste ha ett 'Kontakt önskas'.
      </iso:assert>
      <iso:assert test="count(gn:delsvar[@id='26.2']) le 1">
        'Kontakt önskas' får ha högst ett 'Motivering av kontakt önskas'.
      </iso:assert>
      <iso:let name="delsvarsIdExpr" value="'^26\.[12]$'"/>
      <iso:assert test="count(gn:delsvar[not(matches(@id, $delsvarsIdExpr))]) = 0">
        Oväntat delsvars-id i delsvar till svar "<value-of select="@id"/>". Delsvars-id:n måste matcha "<value-of select="$delsvarsIdExpr"/>".
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q26.1">
    <iso:rule context="//gn:delsvar[@id='26.1']">
      <iso:extends rule="boolean"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q26.2">
    <iso:rule context="//gn:delsvar[@id='26.2']">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q26.1-26.2">
    <iso:rule context="//gn:delsvar[@id='26.1' and (normalize-space(.)='0' or normalize-space(.)='false')]">
      <iso:assert test="count(../gn:delsvar[@id='26.2']) = 0">
        Om 'Kontakt önskas' besvarats med nej kan 'Motivering av kontakt önskas' inte fyllas i.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q9000">
    <iso:rule context="//gn:svar[number(@id) ge 9001]">
      <iso:assert test="count(gn:delsvar[@id = string-join(@id, '.1')]) = 1">
        En tilläggsfråga måste ha ett delsvar med delfråge-id som består av frågans id plus '.1'.
      </iso:assert>
      <iso:assert test="not(preceding-sibling::*/@id = @id)">
        Två tilläggsfrågor kan inte ha samma id.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="q9000.1">
    <iso:rule context="//gn:svar[number(@id) ge 9001]/gn:delsvar">
      <iso:extends rule="non-empty-string"/>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="instans-pattern">
    <iso:rule context="//gn:instans">
      <iso:assert test="number(.) ge 1">
        'Instans' måste vara större än 0.
      </iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="non-empty-string-pattern">
    <iso:rule id="non-empty-string" abstract="true">
      <iso:assert test="count(*) = 0">Värdet får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test="string-length(normalize-space(text())) > 0">Sträng kan inte vara tom.</iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="boolean-pattern">
    <iso:rule id="boolean" abstract="true">
      <iso:assert test="count(*) = 0">Booleskt värde får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test=". castable as xs:boolean">Kan bara vara 'true/1' eller 'false/0'</iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="cv-pattern">
    <iso:rule id="cv" abstract="true">
      <iso:assert test="count(tp:cv) = 1">Ett värde av typen CV måste ha ett cv-element</iso:assert>
      <iso:assert test="count(tp:cv/tp:codeSystem) = 1">codeSystem är obligatoriskt</iso:assert>
      <iso:assert test="tp:cv/tp:codeSystem/count(*) = 0">'codeSystem' får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test="count(tp:cv/tp:code) = 1">code är obligatoriskt</iso:assert>
      <iso:assert test="tp:cv/tp:code/count(*) = 0">'code' får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test="count(tp:cv/tp:displayName) le 1">högst ett displayName kan anges</iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="date-pattern">
    <iso:rule id="date" abstract="true">
      <iso:assert test="count(*) = 0">Datum får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test=". castable as xs:date">Värdet måste vara ett giltigt datum.</iso:assert>
      <iso:assert test="matches(., '^\d{4}-\d\d-\d\d')">Datumet måste uttryckas som YYYY-MM-DD.</iso:assert>
    </iso:rule>
  </iso:pattern>

  <iso:pattern id="period-pattern">
    <iso:rule id="period" abstract="true">
      <iso:assert test="tp:datePeriod">En period måste inneslutas av ett 'datePeriod'-element</iso:assert>
      <iso:assert test="tp:datePeriod/tp:start/count(*) = 0">'from' får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test="tp:datePeriod/tp:start castable as xs:date">'from' måste vara ett giltigt datum.</iso:assert>
      <iso:assert test="matches(tp:datePeriod/tp:start, '^\d{4}-\d\d-\d\d')">'from' måste uttryckas som YYYY-MM-DD.</iso:assert>
      <iso:assert test="tp:datePeriod/tp:end/count(*) = 0">'tom' får inte vara inbäddat i något element.</iso:assert>
      <iso:assert test="tp:datePeriod/tp:end castable as xs:date">'tom' måste vara ett giltigt datum.</iso:assert>
      <iso:assert test="matches(tp:datePeriod/tp:end, '^\d{4}-\d\d-\d\d')">'end' måste uttryckas som YYYY-MM-DD.</iso:assert>
      <iso:assert test="normalize-space(tp:datePeriod/tp:start) le normalize-space(tp:datePeriod/tp:end)">
        'from' måste vara mindre än eller lika med 'to'
      </iso:assert>
    </iso:rule>
  </iso:pattern>

</iso:schema>
