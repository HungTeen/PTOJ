#!/bin/bash

$WORK_PATH/$NACOS_DATA_SHELL --username=$NACOS_USERNAME --password=$NACOS_PASSWORD --filepath=$WORK_PATH/$FILE_2;

sleep 2;

mysql -uroot -p$MYSQL_ROOT_PASSWORD << EOF
system echo '================Start create database ptoj-user====================';
source $WORK_PATH/$USER_SQL;
system echo '================Start create database nacos==================';
source $WORK_PATH/$NACOS_SQL;
system echo '================Start insert user into nacos=================';
source $WORK_PATH/$NACOS_DATA_SQL;
system echo '=====================Everything is ok!=======================';
EOF
