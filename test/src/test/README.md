# FitNesse

## För att köra FitNesse-testerna mha gradle:

##### 1) Starta Statistik
Se README.md i rooten för att se hur Statistik startas.

##### 2) Kör FitNesse-testerna
```sh
./gradlew fitnesseTest
```
eller med följande kommando om Statistik har startats med Intygstjänst
```sh
./gradlew fitnesseTest -DbaseUrl="http://localhost:9101"
```

## För att köra FitNesse-testerna  från FitNesse wiki:

##### 1) Starta Statistik
```sh
./gradlew appRun
```

##### 2) Starta FitNesse wiki
```sh
./gradlew fitnesseWiki
```

##### 3) Kör FitNesse-testerna
- Öppna http://localhost:9125/StatisticsTests i en browser
- Tryck på Suite för att köra alla tester
- Välj ett test i listan och tryck på Test för att köra bara det testet
