### Swagger Documentation:

available at /swagger-ui.html endpoint.

http://localhost:8080/swagger-ui.html


Create docker database:
To create a Docker database, ensure that Docker and docker-compose are installed on your machine. If you have these prerequisites, navigate to the Docker directory and execute the following command:
```shell script
docker compose up -d
```
This command above will automate the image build process for the PostgreSQL database, create the necessary database, and provide additional resources for testing the endpoints.


___________________________

Requires maven 3.8.1 and Java 11

Start the server using
```shell script
mvn spring-boot:run
```
