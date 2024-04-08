FROM mysql:8.2.0

#定义工作目录
ENV WORK_PATH /usr/local/work

#定义会被容器自动执行的目录
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d

#定义sql文件名
ENV USER_SQL user.sql
ENV PROBLEM_SQL problem.sql
ENV TRAINING_SQL training.sql
ENV NACOS_SQL nacos.sql
ENV NACOS_DATA_SQL nacos-data.sql

#定义shell文件名
ENV INSTALL_DATA_SHELL run.sh

#定义生成nacos-data.sql的文件名
ENV NACOS_DATA_SHELL bcrypt

ENV NACOS_USERNAME=${NACOS_USERNAME}
ENV NACOS_PASSWORD=${NACOS_PASSWORD}

COPY ./$USER_SQL $WORK_PATH/
COPY ./$PROBLEM_SQL $WORK_PATH/
COPY ./$TRAINING_SQL $WORK_PATH/
COPY ./$NACOS_SQL $WORK_PATH/
#COPY ./$NACOS_DATA_SQL $WORK_PATH/

COPY ./$INSTALL_DATA_SHELL $AUTO_RUN_DIR/
COPY ./$NACOS_DATA_SHELL  $WORK_PATH/

COPY ./mysql.cnf /etc/mysql/conf.d/

#给执行文件增加可执行权限
RUN chmod a+x $WORK_PATH/$NACOS_DATA_SHELL \
    && echo '' > $WORK_PATH/$NACOS_DATA_SQL \
    && chmod +777 $WORK_PATH/$NACOS_DATA_SQL \
    && chmod a+x $AUTO_RUN_DIR/$INSTALL_DATA_SHELL