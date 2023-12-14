### Swagger Documentation:

This is a simple REST API for the management and booking of properties
The API is documented using Swagger and the documentation is available at the /swagger-ui.html endpoint.

http://localhost:8080/swagger-ui.html



### Running with Docker:
Create docker database & run the app:
To run, ensure that Docker and docker-compose are installed on your machine. If you have these prerequisites, navigate to the Docker directory and execute the following command:
```shell script
docker compose up -d

# or
docker-compose up -d

```
This command above will automate the image build process for the PostgreSQL database, create the necessary database, and provide additional resources for testing the endpoints.
It will also start the docker container for the app

PREPOPULATED USERS ->
```shell script
#DEMO USER
username: user
password: user

#DEMO ADMIN
username: admin
password: user
```


___________________________

### Running app without Docker:
To run the app outside a docker container
Requires maven 3.8.1 and Java 11

Start the server using (ensure DB is running):
```shell script
#start the your local databasedatabase
# or use the dockerized database using
docker-compose up -d booking_pgsql
#or
docker compose up -d booking_pgsql

#and then
DATABASE_URL=localhost mvn spring-boot:run
```

to test the endpoints, use the following command:
```shell script
mvn test
```

---------------------------

Notes

- The API implements an authentication and authorization scheme using JWT
- Endpoints require User to be logged in to access them, except the login and signup endpoints
- Public registration endpoint is provided -> /v1/authentication/signup
- Only admins can create users through POST /users/
