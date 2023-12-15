package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.enums.BlockStatus;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.services.BlockService;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class BlockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlockService blockService;

    @BeforeEach
    public void setup() {
        BlockDTO blockDto = new BlockDTO();
        blockDto.setId(1L);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<BlockDTO> blockList = Collections.singletonList(blockDto);
        Page<BlockDTO> blockPage = new PageImpl<>(blockList);

        when(blockService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(blockPage);
        when(blockService.findById(anyLong(), any(UserDetails.class))).thenReturn(blockDto);
        when(blockService.insert(any(BlockDTO.class), any(UserDetails.class))).thenReturn(blockDto);
        when(blockService.update(any(BlockDTO.class), any(UserDetails.class))).thenReturn(blockDto);
        Mockito.doNothing().when(blockService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldPatchBlock() throws Exception {
        BlockDTO patchedBlock = new BlockDTO();
        patchedBlock.setId(1L);
        patchedBlock.setStatus(BlockStatus.BLOCKED);

        when(blockService.patch(anyLong(), any(BlockDTO.class), any(UserDetails.class))).thenReturn(patchedBlock);

        mockMvc.perform(patch("/v1/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedBlock)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"BLOCKED\"}"));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/blocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnAllBlocks() throws Exception {
        mockMvc.perform(get("/v1/blocks")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnBlockById() throws Exception {
        mockMvc.perform(get("/v1/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewBlock() throws Exception {
        BlockDTO newBlock = new BlockDTO();
        newBlock.setId(1L);
        newBlock.setStatus(BlockStatus.BLOCKED);
        Calendar cal = Calendar.getInstance();
        newBlock.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        newBlock.setEndDate(cal.getTime());
        newBlock.setPropertyId(1L);

        mockMvc.perform(post("/v1/blocks")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newBlock)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateBlock() throws Exception {
        BlockDTO updatedBlock = new BlockDTO();
        updatedBlock.setId(1L);
        updatedBlock.setStatus(BlockStatus.BLOCKED);
        Calendar cal = Calendar.getInstance();
        updatedBlock.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        updatedBlock.setEndDate(cal.getTime());
        updatedBlock.setPropertyId(1L);

        mockMvc.perform(put("/v1/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBlock)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteBlock() throws Exception {
        mockMvc.perform(delete("/v1/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}