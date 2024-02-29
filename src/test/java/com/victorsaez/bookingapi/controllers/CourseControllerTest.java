package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.services.CourseService;
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
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @BeforeEach
    public void setup() {
        CourseDTO courseDto = new CourseDTO();
        courseDto.setId(1L);
        courseDto.setName("Test Course");
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<CourseDTO> courseList = Collections.singletonList(courseDto);
        Page<CourseDTO> coursePage = new PageImpl<>(courseList);

        when(courseService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(coursePage);
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldPatchCourse() throws Exception {
        CourseDTO patchedCourse = new CourseDTO();
        patchedCourse.setId(1L);
        patchedCourse.setName("Patched Course");

        when(courseService.patch(anyLong(), any(CourseDTO.class), any(UserDetails.class))).thenReturn(patchedCourse);

        mockMvc.perform(patch("/v1/courses/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedCourse)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Patched Course\"}"));
    }

    @Test
    public void shouldReturnAllCourses() throws Exception {
        mockMvc.perform(get("/v1/courses")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1,\"name\":\"Test Course\"}]}"));
    }

    @Test
    public void shouldCreateNewCourse() throws Exception {
        CourseDTO newCourse = new CourseDTO();
        newCourse.setId(1L);
        newCourse.setName("Test Course");


        when(courseService.insert(any(CourseDTO.class), any(UserDetails.class))).thenReturn(newCourse);

        mockMvc.perform(post("/v1/courses")
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newCourse)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test Course\"}"));
    }

    @Test
    public void shouldUpdateCourse() throws Exception {
        CourseDTO updatedCourse = new CourseDTO();
        updatedCourse.setId(1L);
        updatedCourse.setName("Updated Course");

        when(courseService.update(any(CourseDTO.class), any(UserDetails.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/v1/courses/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedCourse)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Updated Course\"}"));
    }

    @Test
    public void shouldDeleteCourse() throws Exception {
        Mockito.doNothing().when(courseService).delete(anyLong(), any(UserDetails.class));

        mockMvc.perform(delete("/v1/courses/{id}", 1L)
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isNoContent());
    }
}