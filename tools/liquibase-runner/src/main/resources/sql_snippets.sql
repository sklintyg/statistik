-- Lista duplikat i intyghandelse
select id, correlationId from intyghandelse where type = 0 group by correlationId having count(id) > 1 order by correlationId;
select id, correlationId from intyghandelse where type = 1 group by correlationId having count(id) > 1 order by correlationId;

-- Ta bort duplikat från intyghandelse (har ej verifierat att den gör rätt, men det blir tillräckligt bra för att rensa testdata)
delete from intyghandelse using intyghandelse, intyghandelse i1 where intyghandelse.id > i1.id
      and intyghandelse.correlationId = i1.correlationId and intyghandelse.type = 0;
delete from intyghandelse using intyghandelse, intyghandelse i1 where intyghandelse.id > i1.id
      and intyghandelse.correlationId = i1.correlationId and intyghandelse.type = 1;


drop table aldersgrupp;
drop table DATABASECHANGELOG;
drop table DATABASECHANGELOGLOCK;
drop table diagnosgrupp;
drop table diagnosundergrupp;
drop table handelsepekare;
drop table hsa;
drop table intyghandelse;
drop table sjukfallperlan;
drop table sjukfallpermanad;
drop table sjukfallslangdgrupp;
drop table sjukskrivningsgrad;
drop table wideline;
drop table sjukfall;
drop table enhet;
drop table lakare;
