#!/bin/bash

$WORK_PATH/$NACOS_DATA_SHELL --username=$NACOS_USERNAME --password=$NACOS_PASSWORD --filepath=$WORK_PATH/$NACOS_DATA_SQL;

sleep 2;

mysql -uroot -p$MYSQL_ROOT_PASSWORD << EOF
system echo '================Start create database ptoj-user====================';
source $WORK_PATH/$USER_SQL;
system echo '================Start create database ptoj-problem====================';
source $WORK_PATH/$PROBLEM_SQL;
system echo '================Start create database ptoj-training====================';
source $WORK_PATH/$TRAINING_SQL;
system echo '================Start create database nacos==================';
source $WORK_PATH/$NACOS_SQL;
system echo '================Start insert user into nacos=================';
source $WORK_PATH/$NACOS_DATA_SQL;
system echo '=====================Everything is ok!=======================';
EOF
