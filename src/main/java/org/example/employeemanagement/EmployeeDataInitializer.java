package org.example.employeemanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class EmployeeDataInitializer {
    @Bean
    CommandLineRunner seedEmployees(EmployeeRepository employees) {
        return args -> {
            if (employees.count() == 0) {
                employees.saveAll(List.of(
                        new Employee("EMP-1001", "Ava Patel", "ava.patel@example.com", "Engineering",
                                "Senior Software Engineer", new BigDecimal("112000.00"), true),
                        new Employee("EMP-1002", "Noah Williams", "noah.williams@example.com", "Engineering",
                                "Frontend Developer", new BigDecimal("98000.00"), true),
                        new Employee("EMP-1003", "Mia Chen", "mia.chen@example.com", "Human Resources",
                                "HR Manager", new BigDecimal("87000.00"), true),
                        new Employee("EMP-1004", "Liam Johnson", "liam.johnson@example.com", "Finance",
                                "Financial Analyst", new BigDecimal("79500.00"), true),
                        new Employee("EMP-1005", "Sophia Garcia", "sophia.garcia@example.com", "Marketing",
                                "Marketing Specialist", new BigDecimal("72000.00"), false),
                        new Employee("EMP-1006", "Ethan Brown", "ethan.brown@example.com", "Design",
                                "Product Designer", new BigDecimal("91000.00"), true)
                ));
            }
        };
    }
}
