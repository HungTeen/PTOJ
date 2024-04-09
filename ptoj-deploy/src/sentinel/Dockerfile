FROM ibm-semeru-runtimes:open-8u402-b06-jdk-jammy

ENV JAVA_OPTS="-Dsentinel.dashboard.auth.username=sentinel -Dsentinel.dashboard.auth.password=sentinel"

RUN mkdir -p /pangteen/sentinel

WORKDIR /pangteen/sentinel

ADD sentinel-dashboard-1.8.7.jar ./app.jar

EXPOSE 8858

EXPOSE 8719

CMD java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dserver.port=8858 -Dcsp.sentinel.api.port=8719 -Dcsp.sentinel.dashboard.server=localhost:8858 -Dproject.name=sentinel-dashboard -jar app.jar