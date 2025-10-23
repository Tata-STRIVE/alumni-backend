package com.striveconnect.controller;

import com.striveconnect.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @Test
    @WithMockUser(authorities = "ALUMNUS")
    void whenAlumnusAccessesJobs_thenOk() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ALUMNUS")
    void whenAlumnusAppliesForJob_thenOk() throws Exception {
        mockMvc.perform(post("/api/jobs/1/apply"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ALUMNUS")
    void whenAlumnusTriesToCreateJob_thenForbidden() throws Exception {
        // Alumnus should NOT be able to create jobs.
        String newJobJson = "{\"title\":\"New Job\",\"companyName\":\"New Co\",\"location\":\"City\",\"description\":\"Desc\"}";
        mockMvc.perform(post("/api/jobs")
                .contentType("application/json")
                .content(newJobJson))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(authorities = "CENTER_ADMIN")
    void whenAdminTriesToCreateJob_thenCreated() throws Exception {
        String newJobJson = "{\"title\":\"New Job\",\"companyName\":\"New Co\",\"location\":\"City\",\"description\":\"Desc\"}";
        mockMvc.perform(post("/api/jobs")
                .contentType("application/json")
                .content(newJobJson))
                .andExpect(status().isCreated());
    }
    
    @Test
    void whenUnauthenticatedUserAccessesJobs_thenUnauthorized() throws Exception {
        // No @WithMockUser means the user is not logged in.
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isUnauthorized());
    }
}
