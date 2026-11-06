package org.example.employeemanagement;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository employees;

    public EmployeeController(EmployeeRepository employees) {
        this.employees = employees;
    }

    @GetMapping
    public List<Employee> getAll() {
        return employees.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable String id) {
        return findEmployee(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody Employee employee) {
        if (employees.existsById(employee.getEmployeeId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee ID already exists");
        }
        return employees.save(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable String id, @Valid @RequestBody Employee changes) {
        Employee employee = findEmployee(id);
        if (!id.equals(changes.getEmployeeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID cannot be changed");
        }
        employee.setName(changes.getName());
        employee.setEmail(changes.getEmail());
        employee.setDepartment(changes.getDepartment());
        employee.setDesignation(changes.getDesignation());
        employee.setSalary(changes.getSalary());
        employee.setActive(changes.isActive());
        return employees.save(employee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        employees.delete(findEmployee(id));
    }

    @GetMapping("/count")
    public long getCount() {
        return employees.count();
    }

    @GetMapping("/department/{department}")
    public List<Employee> getByDepartment(@PathVariable String department) {
        return employees.findAllByDepartmentIgnoreCaseOrderByNameAsc(department);
    }

    @GetMapping("/active/count")
    public long getActiveCount() {
        return employees.countByActiveTrue();
    }

    private Employee findEmployee(String id) {
        return employees.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }
}
