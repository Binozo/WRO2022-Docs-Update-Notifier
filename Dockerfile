FROM --platform=linux/amd64 gradle:jdk17-alpine AS BUILDER

RUN mkdir /app
COPY . /app
WORKDIR /app

RUN gradle build
RUN gradle fatJar

FROM openjdk:17

LABEL maintainer=binozoworks
LABEL org.opencontainers.image.source="https://github.com/Binozo/ WRO2022-Docs-Update-Notifier"
LABEL org.opencontainers.image.description="A Discord Bot which notifies you if a change of documents on the WRO DE Homepage has been detected."

COPY --from=BUILDER /app/build/libs/WRO2022_docs_update_notifier-1.0-SNAPSHOT-standalone.jar "update-notifier.jar"

CMD ["java", "-jar", "update-notifier.jar"]
