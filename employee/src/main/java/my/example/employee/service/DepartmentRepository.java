package my.example.employee.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import my.example.employee.model.Department;

public interface DepartmentRepository extends MongoRepository<Department, Long> {

}
