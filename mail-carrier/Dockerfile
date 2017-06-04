FROM java:8-jre

ENV VERTICLE_FILE target/mail-carrier-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

EXPOSE 8087

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY docker.json $VERTICLE_HOME/


WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar mail-carrier-fat.jar -cluster -ha -conf docker.json"]
