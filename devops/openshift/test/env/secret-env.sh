#!/bin/bash
# Assign backing service addresses from the outer environment

export DB_USERNAME=${DATABASE_USERNAME:-statistik}
export DB_PASSWORD=${DATABASE_PASSWORD:-statistik}
export DB_NAME=${DATABASE_NAME:-statistik_test}
export DB_SERVER=$MYSQL_SERVICE_HOST
export DB_PORT=$MYSQL_SERVICE_PORT

export ACTIVEMQ_BROKER_USERNAME=${ACTIVEMQ_BROKER_USERNAME:-admin}
export ACTIVEMQ_BROKER_PASSWORD=${ACTIVEMQ_BROKER_PASSWORD:-admin}

export REDIS_PASSWORD=${REDIS_PASSWORD:-redis}
export REDIS_PORT=$REDIS_SERVICE_PORT
export REDIS_HOST=$REDIS_SERVICE_HOST

SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-test,caching-enabled,testapi,hsa-stub,wc-hsa-stub,security-fake}"

export CATALINA_OPTS_APPEND="\
-Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
-Dstatistics.config.folder=/opt/$APP_NAME/config \
-Dstatistics.config.file=/opt/$APP_NAME/config/statistik.properties \
-Dcertificate.logback.file=classpath:logback-ocp.xml \
-Dcertificate.folder=/opt/$APP_NAME/certifikat \
-Dstatistics.credentials.file=/opt/$APP_NAME/env/secret-env.properties \
-Dstatistics.resources.folder=/tmp/resources \
-Dfile.encoding=UTF-8 \
-DbaseUrl=http://${APP_NAME}:8080"
