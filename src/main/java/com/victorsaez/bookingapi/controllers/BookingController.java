package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.BookingDTO;
import com.victorsaez.bookingapi.entities.Booking;
import com.victorsaez.bookingapi.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Bookings")
@RequestMapping(value = "/v1/bookings", produces = "application/json")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all bookings in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<BookingDTO>> findAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
         return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns booking by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Booking not found.")
    })
    public ResponseEntity<BookingDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @PostMapping
    @Operation(summary = "Create a new booking in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created with success."),
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BookingDTO> insert(@RequestBody @Valid BookingDTO booking, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdBooking = service.insert(booking, currentUserDetails);
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
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BookingDTO> update(@PathVariable Long id, @RequestBody @Valid BookingDTO booking, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        booking.setId(id);
        var updatedBooking = service.update(booking, currentUserDetails);
        return ResponseEntity.ok().body(updatedBooking);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete booking by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Booking not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }
}