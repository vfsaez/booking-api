package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.StudentDTO;
import com.victorsaez.bookingapi.exceptions.AgeRequirementsException;
import com.victorsaez.bookingapi.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @BeforeEach
    public void setup() throws AgeRequirementsException {
        StudentDTO studentDto = new StudentDTO();
        studentDto.setId(1L);
        studentDto.setName("Charles");
        studentDto.setFamilyName("Demo");
        studentDto.setDateOfBirth(new Date());


        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<StudentDTO> studentList = Collections.singletonList(studentDto);
        Page<StudentDTO> studentPage = new PageImpl<>(studentList);

        when(studentService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(studentPage);
        when(studentService.findById(anyLong(), any(UserDetails.class))).thenReturn(studentDto);
        when(studentService.insert(any(StudentDTO.class), any(UserDetails.class))).thenReturn(studentDto);
        when(studentService.update(any(StudentDTO.class), any(UserDetails.class))).thenReturn(studentDto);
        when(studentService.patch(anyLong(), any(StudentDTO.class), any(UserDetails.class))).thenReturn(studentDto);
        Mockito.doNothing().when(studentService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnAllStudents() throws Exception {
        mockMvc.perform(get("/v1/students")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnStudentById() throws Exception {
        mockMvc.perform(get("/v1/students/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldPatchStudent() throws Exception {
        StudentDTO patchedStudent = new StudentDTO();
        patchedStudent.setId(1L);
        patchedStudent.setName("Patched Student");

        when(studentService.patch(anyLong(), any(StudentDTO.class), any(UserDetails.class))).thenReturn(patchedStudent);

        mockMvc.perform(patch("/v1/students/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedStudent)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Patched Student\"}"));
    }
    @Test
    public void shouldCreateNewStudent() throws Exception {
        StudentDTO newStudent = new StudentDTO();
        newStudent.setId(1L);
        newStudent.setName("John");
        newStudent.setFamilyName("Student");
        newStudent.setEmail("updated@validemail.com");
        newStudent.setDateOfBirth(new Date());

        mockMvc.perform(post("/v1/students")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateStudent() throws Exception {
        StudentDTO updatedStudent = new StudentDTO();
        updatedStudent.setId(1L);
        updatedStudent.setName("JohnUpdated");
        updatedStudent.setFamilyName("StudentUpdated");
        updatedStudent.setEmail("updated@validemail.com");
        updatedStudent.setDateOfBirth(new Date());

        mockMvc.perform(put("/v1/students/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteStudent() throws Exception {
        mockMvc.perform(delete("/v1/students/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}