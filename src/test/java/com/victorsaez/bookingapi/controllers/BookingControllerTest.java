package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.services.BookingService;
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
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    public void setup() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<BookingDTO> bookingList = Collections.singletonList(bookingDTO);
        Page<BookingDTO> bookingPage = new PageImpl<>(bookingList);

        Mockito.when(bookingService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(bookingPage);
        Mockito.when(bookingService.findById(anyLong(), any(UserDetails.class))).thenReturn(bookingDTO);
        Mockito.when(bookingService.insert(any(BookingDTO.class), any(UserDetails.class))).thenReturn(bookingDTO);
        Mockito.when(bookingService.update(any(BookingDTO.class), any(UserDetails.class))).thenReturn(bookingDTO);
        Mockito.doNothing().when(bookingService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnAllBookings() throws Exception {
        mockMvc.perform(get("/bookings")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnBookingById() throws Exception {
        mockMvc.perform(get("/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewBooking() throws Exception {
        BookingDTO newBooking = new BookingDTO();
        newBooking.setId(1L);

        mockMvc.perform(post("/bookings")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newBooking)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldUpdateBooking() throws Exception {
        BookingDTO updatedBooking = new BookingDTO();
        updatedBooking.setId(1L);

        mockMvc.perform(put("/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}