#!/bin/bash
# Assign backing service addresses from the outer environment

export DB_USERNAME=${DATABASE_USERNAME-statistik}
export DB_PASSWORD=${DATABASE_PASSWORD-statistik}
export DB_NAME=${DATABASE_NAME-statistik_test}
export DB_SERVER=$MYSQL_SERVICE_HOST
export DB_PORT=$MYSQL_SERVICE_PORT

export JMS_RECEIVER_QUEUE_NAME=test.statistik.utlatande.queue
export JMS_BROKER_USERNAME=${ACTIVEMQ_BROKER_USERNAME-admin}
export JMS_BROKER_PASSWORD=${ACTIVEMQ_BROKER_PASSWORD-admin}

export REDIS_PASSWORD=${REDIS_PASSWORD-redis}
export REDIS_PORT=$REDIS_SERVICE_PORT
export REDIS_HOST=$REDIS_SERVICE_HOST

export SPRING_PROFILES_ACTIVE="dev,testapi,hsa-stub,wc-hsa-stub,security-fake,active,hsacached"

export CATALINA_OPTS_APPEND="\
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5008 \
-Dstatistics.config.folder=/opt/$APP_NAME/config \
-Dstatistics.config.file=/opt/$APP_NAME/config/statistik.properties \
-Dcertificate.folder=/opt/$APP_NAME/env \
-Dstatistics.credentials.file=/opt/$APP_NAME/env/secret-env.properties \
-Dstatistics.resources.folder=/tmp/resources
-DbaseUrl=http://${APP_NAME}:8080"
