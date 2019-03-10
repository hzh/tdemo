package my.example.employee.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import my.example.employee.model.Employee;

interface EmployeeRepository extends MongoRepository<Employee, String> {
}
