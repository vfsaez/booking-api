package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.BlockDTO;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setId(1L);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<BlockDTO> blockList = Collections.singletonList(blockDTO);
        Page<BlockDTO> blockPage = new PageImpl<>(blockList);

        Mockito.when(blockService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(blockPage);
        Mockito.when(blockService.findById(anyLong(), any(UserDetails.class))).thenReturn(blockDTO);
        Mockito.when(blockService.insert(any(BlockDTO.class), any(UserDetails.class))).thenReturn(blockDTO);
        Mockito.when(blockService.update(any(BlockDTO.class), any(UserDetails.class))).thenReturn(blockDTO);
        Mockito.doNothing().when(blockService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnAllBlocks() throws Exception {
        mockMvc.perform(get("/blocks")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnBlockById() throws Exception {
        mockMvc.perform(get("/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewBlock() throws Exception {
        BlockDTO newBlock = new BlockDTO();
        newBlock.setId(1L);

        mockMvc.perform(post("/blocks")
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

        mockMvc.perform(put("/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBlock)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteBlock() throws Exception {
        mockMvc.perform(delete("/blocks/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}