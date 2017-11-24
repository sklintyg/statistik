Rensning av vårdgivare

I enlighet med jira [STATISTIK-1237](https://inera-certificate.atlassian.net/browse/STATISTIK-1237)  ska intyget inte längre vara källan för vilken vårdgivare ett enhet hör till, utan den informationen ska istället hämtas från HSA.

Detta skript rensar vårdgivar-data från databasen för att förbereda för intygsstatistik att åter populera denna data från HSA vid nästa omprocessning av intygen.