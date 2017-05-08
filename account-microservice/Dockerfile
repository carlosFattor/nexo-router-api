FROM java:8-jre

ENV VERTICLE_FILE target/account-microservice-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

EXPOSE 8085

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY docker.json $VERTICLE_HOME/


WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar account-microservice-fat.jar -cluster -ha -conf docker.json"]
