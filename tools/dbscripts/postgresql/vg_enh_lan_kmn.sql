--
-- Tar fram statistik på enhet per vårdgivare, län och kommun. Förutsätter att hsa-tabellen finns importerad från mysql.
--

create table enheter (
  vardgivare VARCHAR(255),
  enhet VARCHAR(255),
  lan VARCHAR(255),
  kommun VARCHAR(255));

insert into enheter(vardgivare, enhet, lan, kommun)
  select JSON(data)#>'{vardgivare,id}', JSON(data)#>'{enhet,id}', JSON(data)#>'{enhet,geografi,lan}', JSON(data)#>'{enhet,geografi,kommun}'
  from hsa;

select vardgivare, enhet, lan, kommun, count (*) from enheter group by vardgivare, enhet, lan, kommun;

truncate enheter;

drop table enheter;