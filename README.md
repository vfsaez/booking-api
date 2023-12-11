### Swagger Documentation:

This is a simple REST API for the management and booking of properties
The API is documented using Swagger and the documentation is available at the /swagger-ui.html endpoint.

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

to test the endpoints, use the following command:
```shell script
mvn test
```

---------------------------

Notes

The API implements an authentication and authorization scheme using JWT
Endpoints require User to be logged in to access them, except the session creation/login endpoint
-- No public registration endpoint is provided
There are two test files as examples

 

