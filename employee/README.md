# Employee Service

### Summary

The service provides following REST endpoints:

* `GET /employees/{id}` : retrieve data of a single employee identified by employee id
* `POST /employees` : create new employee, accepts employee data as json payload
* `PUT /employees/{id}` : update or create new employee, accepts employee data as json payload
* `DELETE /employees/{id}` : delete employee data by employee id
* `GET /departments` : retrieve data of all departments
* `GET /departments/{id}` : retrieve data of a single department identified by department id
* `POST /departments` : create a new department

### Authentication

The /employees endpoints are secured by http basic authentication. User name and password are defined in application.properties.