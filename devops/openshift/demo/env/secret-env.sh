#!/bin/bash

export CATALINA_OPTS_APPEND="\
-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
-Dstatistics.config.folder=/opt/$APP_NAME/config \
-Dstatistics.config.file=/opt/$APP_NAME/config/statistik.properties \
-Dcertificate.logback.file=/opt/$APP_NAME/config/logback-ocp.xml \
-Dcertificate.folder=/opt/$APP_NAME/certifikat \
-Dstatistics.credentials.file=/opt/$APP_NAME/env/secret-env.properties \
-Dstatistics.resources.folder=classpath: \
-Dfile.encoding=UTF-8 \
-DbaseUrl=http://${APP_NAME}:8080"
