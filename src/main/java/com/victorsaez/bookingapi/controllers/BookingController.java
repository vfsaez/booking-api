package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Bookings")
@RequestMapping(value = "/bookings", produces = "application/json")
public class BookingController {

    private final BookingService service;



    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all bookings in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    public ResponseEntity<List<BookingDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns booking by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Booking not found.")
    })
    public ResponseEntity<BookingDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new booking in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BookingDTO> insert (@RequestBody @Valid BookingDTO booking) {
        var createdBooking = service.insert(booking);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdBooking.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdBooking);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a booking in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking updated successfully."),
            @ApiResponse(responseCode = "404", description = "Booking not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BookingDTO> update(@PathVariable Long id, @RequestBody @Valid BookingDTO booking) {
        booking.setId(id);
        var updatedBooking = service.update(booking);
        return ResponseEntity.ok().body(updatedBooking);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete booking by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Booking not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}