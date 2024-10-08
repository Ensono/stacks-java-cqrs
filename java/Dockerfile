FROM azul/zulu-openjdk-alpine:17.0.9 as build
WORKDIR /workspace/app

# Copy the m2 directory if it exists to re-use an external cache.
# The `mvnw` file is also copied so that if an `.m2` directory isn't present
# the command won't fail.
COPY mvnw .m2* /root/.m2/repository/

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw dependency:go-offline --quiet \
    && ./mvnw process-resources --quiet \
    && ./mvnw clean package -Dmaven.default-test.skip=true -Dmaven.test.skip=true -DskipTests --no-transfer-progress --quiet \
    && mkdir -p extracted && find target -maxdepth 1 -type f -name '*.jar' ! -name '*-javadoc.jar' ! -name '*-sources.jar' -exec java -Djarmode=layertools -jar {} extract --destination extracted \;

FROM azul/zulu-openjdk-alpine:17-jre

VOLUME /tmp

ARG DEPENDENCY=/workspace/app/extracted

COPY --from=build ${DEPENDENCY}/dependencies/ ./
RUN true
COPY --from=build ${DEPENDENCY}/spring-boot-loader/ ./
RUN true
COPY --from=build ${DEPENDENCY}/snapshot-dependencies/ ./
RUN true
COPY --from=build ${DEPENDENCY}/application/ ./

RUN if [ -e "/BOOT-INF/classes/emulatorcert.crt" ]; then cd $JAVA_HOME/lib/security && keytool -importcert -cacerts -storepass changeit -noprompt -alias documentdbemulator -file /BOOT-INF/classes/emulatorcert.crt; fi

ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]

EXPOSE 9000
