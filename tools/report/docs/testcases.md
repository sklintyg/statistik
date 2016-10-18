

#Kontrollera antal sjukfall på nmt-vg1 för period mars-maj 2016:

1. Hämta hem databasdump för året 2016 och vårdgivare nmt-vg1(TSTNMT2321000156-1002):
	./report.py -i 2016-01-01:2016-12-31 -f result.txt -v TSTNMT2321000156-1003 -w <lösenord> -b mysql.ip20.nordicmedtest.se

2. Kör skriptet mot resultatet (result.txt) med -s (gruppera på sjukfall) och sätt datumintervall till mars-maj
	./report.py -i 2016-03-01:2016-05-31 -s < result.txt 

3. Jämför med statistikapplikationen
