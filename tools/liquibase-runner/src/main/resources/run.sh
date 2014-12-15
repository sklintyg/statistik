#!/bin/sh
java -jar liquibase-core-2.0.5.jar --classpath=.:mysql-connector-java-5.1.32.jar --changeLogFile="changelog/changelog.xml" --url=jdbc:mysql://localhost:3306/statistik?useCompression=true --contexts=none --username=statistik --password=statistik update
