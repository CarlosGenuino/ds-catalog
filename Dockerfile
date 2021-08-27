FROM openjdk:13-alpine
LABEL maintainer="carloxsgenuino3@live.com"

ENV LANG C.UTF-8

VOLUME /tmp

COPY target/*.jar /app/app.jar

# Ao executar o jar a tag $APP_OPTIONS permite a passagem de outros comandos
CMD java -jar /app/app.jar $APP_OPTIONS