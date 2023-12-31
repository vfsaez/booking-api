package com.victorsaez.bookingapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.enums.BookingStatus;
import com.victorsaez.bookingapi.exceptions.AccessDeniedException;
import com.victorsaez.bookingapi.exceptions.PropertyNotAvailableException;
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
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    public void setup() {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("testUser");

        List<BookingDTO> bookingList = Collections.singletonList(bookingDto);
        Page<BookingDTO> bookingPage = new PageImpl<>(bookingList);

        when(bookingService.findAll(any(Pageable.class), any(UserDetails.class))).thenReturn(bookingPage);
        when(bookingService.findById(anyLong(), any(UserDetails.class))).thenReturn(bookingDto);
        when(bookingService.insert(any(BookingDTO.class), any(UserDetails.class))).thenReturn(bookingDto);
        when(bookingService.update(any(BookingDTO.class), any(UserDetails.class))).thenReturn(bookingDto);
        Mockito.doNothing().when(bookingService).delete(anyLong(), any(UserDetails.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldPatchBooking() throws Exception {
        BookingDTO patchedBooking = new BookingDTO();
        patchedBooking.setId(1L);
        patchedBooking.setStatus(BookingStatus.BOOKED);

        when(bookingService.patch(anyLong(), any(BookingDTO.class), any(UserDetails.class))).thenReturn(patchedBooking);

        mockMvc.perform(patch("/v1/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(patchedBooking)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"BOOKED\"}"));
    }

    @Test
    public void shouldReturnAllBookings() throws Exception {
        mockMvc.perform(get("/v1/bookings")
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"content\":[{\"id\":1}]}"));
    }

    @Test
    public void shouldReturnBookingById() throws Exception {
        mockMvc.perform(get("/v1/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldCreateNewBooking() throws Exception {
        BookingDTO newBooking = new BookingDTO();
        newBooking.setId(1L);
        newBooking.setStatus(BookingStatus.BOOKED);
        Calendar cal = Calendar.getInstance();
        newBooking.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        newBooking.setEndDate(cal.getTime());
        newBooking.setPropertyId(1L);
        newBooking.setClientId(1L);

        mockMvc.perform(post("/v1/bookings")
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
        updatedBooking.setStatus(BookingStatus.BOOKED);
        Calendar cal = Calendar.getInstance();
        updatedBooking.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 1);
        updatedBooking.setEndDate(cal.getTime());
        updatedBooking.setPropertyId(1L);
        updatedBooking.setClientId(1L);

        mockMvc.perform(put("/v1/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBooking)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1}"));
    }

    @Test
    public void shouldDeleteBooking() throws Exception {
        mockMvc.perform(delete("/v1/bookings/{id}", 1L)
                        .with(user("testUser").roles("USER"))  // Mock a user
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldRebookBooking() throws Exception {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.BOOKED);

        when(bookingService.rebook(anyLong(), any(UserDetails.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/v1/bookings/{id}/rebook", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"BOOKED\"}"));
    }

    @Test
    public void shouldThrowExceptionWhenRebookingUnavailableProperty() throws Exception {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.CANCELLED);
        PropertyNotAvailableException ex = new PropertyNotAvailableException();

        when(bookingService.rebook(anyLong(), any(UserDetails.class))).thenThrow(ex);

        mockMvc.perform(post("/v1/bookings/{id}/rebook", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCancelBooking() throws Exception {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.CANCELLED);

        when(bookingService.cancel(anyLong(), any(UserDetails.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/v1/bookings/{id}/cancel", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"CANCELLED\"}"));
    }

    @Test
    public void shouldThrowExceptionWhenNonAdminUserTriesToCancelBooking() throws Exception {
        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.BOOKED);

        when(bookingService.cancel(anyLong(), any(UserDetails.class))).thenThrow(AccessDeniedException.class);

        mockMvc.perform(post("/v1/bookings/{id}/cancel", 1L)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}