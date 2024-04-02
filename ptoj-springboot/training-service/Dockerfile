FROM openjdk:11
MAINTAINER PangTeen
LABEL authors="PangTeen"
WORKDIR /
COPY target/training-service.jar app.jar
EXPOSE 8003
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]