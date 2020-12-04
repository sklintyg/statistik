#!/bin/bash

APP_DIR="/opt/$APP_NAME"

export CATALINA_OPTS_APPEND="\
-Dapplication.dir=$APP_DIR \
-Dlogback.file=$APP_DIR/config/logback-ocp.xml \
-Djava.awt.headless=true \
-Dfile.encoding=UTF-8 \
-DbaseUrl=http://${APP_NAME}:8080"
