-- Räkna antal av olika kön för hsa-personal
SELECT  count(*) as o FROM hsa WHERE json_extract_path_text(data, 'personal', 'kon') ='0';
SELECT  count(*) as k FROM hsa WHERE json_extract_path_text(data, 'personal', 'kon') ='1';
SELECT  count(*) as m FROM hsa WHERE json_extract_path_text(data, 'personal', 'kon') ='2';
SELECT  count(*) as n FROM hsa WHERE json_extract_path_text(data, 'personal', 'kon') NOT IN ('0','1','2');
SELECT  count(*) as n FROM hsa WHERE json_extract_path_text(data, 'personal', 'kon') IS NULL ;
