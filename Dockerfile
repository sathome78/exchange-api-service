FROM java:8
VOLUME /tmp
ARG APP_PATH=/exrates-api-service
ARG ENVIRONMENT

RUN mkdir -p exrates-api-service
COPY ./target/exrates-api-service-2.0.5.RELEASE.jar ${APP_PATH}/exrates-api-service-2.0.5.RELEASE.jar
COPY ./target/config/${ENVIRONMENT}/application.properties ${APP_PATH}/application.properties
WORKDIR ${APP_PATH}
RUN readlink -f application.properties
EXPOSE 8080
CMD java -jar exrates-api-service-2.0.5.RELEASE.jar

