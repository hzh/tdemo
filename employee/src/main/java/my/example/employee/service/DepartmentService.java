package my.example.employee.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import my.example.employee.model.Department;

@RestController
@RequestMapping(path = "departments", produces = "application/json")
public class DepartmentService {
	
	@Autowired
	DepartmentRepository departmentRepo;
	
	@GetMapping
	public List<Department> findAllDepartments() {
		return departmentRepo.findAll();
	}
	
	@PostMapping
	public ResponseEntity<String> createDepartment(@RequestBody Department department) {
		Department saved = departmentRepo.insert(department);
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.header(HttpHeaders.LOCATION, "/departments/"+saved.getId())
				.build();
	}
}
