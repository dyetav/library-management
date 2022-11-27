FROM adoptopenjdk/openjdk11:alpine
RUN mkdir /opt/app
RUN apk update && apk upgrade && apk add bash
COPY target/library-management-0.0.1-SNAPSHOT.jar /opt/app
COPY ./wait-for-it.sh /opt/app
RUN chmod 764 /opt/app/wait-for-it.sh
ENTRYPOINT ["/opt/app/wait-for-it.sh", "mysql-test:3306", "--", "java", "-jar", "/opt/app/library-management-0.0.1-SNAPSHOT.jar"]