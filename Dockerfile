FROM openjdk:8
VOLUME /tmp
ADD target/clklog-api-1.1.0.jar /app.jar
EXPOSE 8087
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]