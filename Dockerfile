FROM java:8
VOLUME /tmp
ARG APP_PATH=/exrates-api-service
ARG ENVIRONMENT

RUN mkdir -p exrates-api-service
COPY ./target/api-service.jar ${APP_PATH}/api-service.jar
COPY ./target/config/${ENVIRONMENT}/application.properties ${APP_PATH}/application.properties
ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.properties

WORKDIR ${APP_PATH}
RUN readlink -f application.properties
EXPOSE 8080
CMD java -jar api-service.jar

