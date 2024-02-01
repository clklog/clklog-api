FROM ubuntu-latest
VOLUME /tmp
ADD staging/clklog-api-1.1.0.jar /app.jar
EXPOSE 8087
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
