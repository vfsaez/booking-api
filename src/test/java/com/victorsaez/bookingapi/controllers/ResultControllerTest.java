package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.ResultDTO;
import com.victorsaez.bookingapi.enums.ResultGrade;
import com.victorsaez.bookingapi.services.ResultService;
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
public class ResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultService resultService;

    @BeforeEach
    public void setup() {
        ResultDTO resultDto = new ResultDTO();
        resultDto.setId(1L);
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<ResultDTO> resultList = Collections.singletonList(resultDto);
        Page<ResultDTO> resultPage = new PageImpl<>(resultList);

        when(resultService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(resultPage);
        when(resultService.findById(anyLong(), any(UserDetails.class))).thenReturn(resultDto);
        when(resultService.insert(any(ResultDTO.class), any(UserDetails.class))).thenReturn(resultDto);
        when(resultService.update(any(ResultDTO.class), any(UserDetails.class))).thenReturn(resultDto);
        Mockito.doNothing().when(resultService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/results")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldPatchResult() throws Exception {
        ResultDTO patchedResult = new ResultDTO();
        patchedResult.setId(1L);
        patchedResult.setGrade(ResultGrade.A);

        when(resultService.patch(anyLong(), any(ResultDTO.class), any(UserDetails.class))).thenReturn(patchedResult);

        mockMvc.perform(patch("/v1/results/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedResult)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"grade\":\"A\"}"));
    }

    @Test
    public void shouldReturnAllResults() throws Exception {
        mockMvc.perform(get("/v1/results")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnResultById() throws Exception {
        mockMvc.perform(get("/v1/results/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewResult() throws Exception {
        ResultDTO newResult = new ResultDTO();
        newResult.setId(1L);
        newResult.setGrade(ResultGrade.A);
        newResult.setCourseId(1L);
        newResult.setStudentId(1L);

        mockMvc.perform(post("/v1/results")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newResult)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateResult() throws Exception {
        ResultDTO updatedResult = new ResultDTO();
        updatedResult.setId(1L);
        updatedResult.setGrade(ResultGrade.B);
        updatedResult.setCourseId(1L);
        updatedResult.setStudentId(1L);

        mockMvc.perform(put("/v1/results/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedResult)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteResult() throws Exception {
        mockMvc.perform(delete("/v1/results/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}