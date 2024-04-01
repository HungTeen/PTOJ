FROM openjdk:11
MAINTAINER PangTeen
LABEL authors="PangTeen"
WORKDIR /
COPY target/ptoj-gateway.jar app.jar
EXPOSE 6688
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]