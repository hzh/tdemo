# Employee & Event Service

### Summary

The system runs on Docker/Docker compose, please make sure that [Docker](https://www.docker.com/) is installed.

The system consists of 2 REST service, 2 MongoDB instance2, 1 RabbitMq instance and 1 non-lasting instance for integration test.

To start up the system, run command:

`docker-compose up --build`

Details can be found in `docker-compose.yml`. 

### Employee service

Published to http://localhost:18080/employees

Details see [README.md](employee/README.md)

### Event service

Published to http://localhost:28080/events

Details see [README.md](event/README.md)

### Postman scripts

Postman test scripts for the REST endpoints: 

- [Test suite](test-suite.postman_collection.json)
- [Environment](local-docker-env.postman_environment.json)

### Integration test

Integration test will be performed after all services are available. It may try multiple times before the test is successful.

The integration test can be run separately after the system is up by:

`docker-compose up integration_test`
