#!/bin/sh

export SPEECH_APP_SERVER_PORT=9091
export SPEECH_APP_SCHEMA_LOCATIONS="classpath:ddl/00_create_schema.sql, classpath:ddl/01_create_ddl_H2.sql"
export SPRING_SQL_INIT_MODE=always
export HIBERNATE_DDL_AUTO=none
export HIBERNATE_SHOW_SQL=false
export SPEECH_APP_DB_URL="jdbc:h2:file:./speech_db"

exec java -jar ls-speech-api.jar

