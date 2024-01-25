@echo off

set SPEECH_APP_SERVER_PORT=9091
set SPEECH_APP_SCHEMA_LOCATIONS=classpath:ddl/00_create_schema.sql, classpath:ddl/01_create_ddl_H2.sql
set SPRING_SQL_INIT_MODE=always
set HIBERNATE_DDL_AUTO=none
set HIBERNATE_SHOW_SQL=false
set SPEECH_APP_DB_URL=jdbc:h2:file:./speech_db

call java -jar ls-speech-api.jar
