#!/bin/sh
java -jar target/liquibase-runner-for-statistics-1.1-SNAPSHOT-jar-with-dependencies.jar --changeLogFile="changelog/changelog.xml" --contexts=none update
