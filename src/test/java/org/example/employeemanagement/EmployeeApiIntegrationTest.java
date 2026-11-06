package org.example.employeemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void seededEmployeesAndCrudEndpointsWork() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));

        mockMvc.perform(get("/api/employees/count"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("6"));

        mockMvc.perform(get("/api/employees/department/Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        String employee = """
                {
                  "employeeId": "EMP-2001",
                  "name": "Jordan Lee",
                  "email": "jordan.lee@example.com",
                  "department": "Product",
                  "designation": "Product Manager",
                  "salary": 105000,
                  "active": true
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employee))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value("EMP-2001"));

        mockMvc.perform(get("/api/employees/EMP-2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jordan Lee"));

        String updatedEmployee = employee.replace("Jordan Lee", "Jordan Lee-Smith");
        mockMvc.perform(put("/api/employees/EMP-2001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployee))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jordan Lee-Smith"));

        mockMvc.perform(delete("/api/employees/EMP-2001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/employees/EMP-2001"))
                .andExpect(status().isNotFound());
    }
}
