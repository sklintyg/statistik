#!/bin/sh
java -jar target/statistik-liquibase-runner-1.1-SNAPSHOT-jar-with-dependencies.jar --changeLogFile="changelog/changelog.xml" --contexts=none update
