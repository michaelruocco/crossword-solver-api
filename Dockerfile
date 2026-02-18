FROM michaelruocco/tesseract-java:5.5.1-jre25-runtime

COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","--enable-native-access=ALL-UNNAMED","-XX:+UseContainerSupport","-jar","/app/app.jar"]
