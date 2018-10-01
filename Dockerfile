FROM openjdk:10
VOLUME /tmp
ARG APP_PATH=/api-service
ARG ENVIRONMENT

RUN mkdir -p api-service
COPY ./target/api-service.jar ${APP_PATH}/api-service.jar
COPY ./target/config/dev/application.yml ${APP_PATH}/application.yml
ARG CONFIG_FILE_PATH="-Dspring.config.location="${ENVIRONMENT}"/application.yml"

WORKDIR ${APP_PATH}

EXPOSE 8081
CMD java -jar api-service.jar $CONFIG_FILE_PATH