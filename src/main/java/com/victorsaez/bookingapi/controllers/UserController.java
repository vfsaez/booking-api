package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.UserDTO;
import com.victorsaez.bookingapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;


@RestController
@Tag(name = "Users")
@RequestMapping(value = "/users",  produces = "application/json")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all users in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public ResponseEntity<UserDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
           return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public void delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
    }

    @PostMapping
    @Operation(summary = "Create a new user in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<UserDTO> insert(@RequestBody @Valid UserDTO user, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdUser = service.insert(user, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdUser);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a user in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully."),
            @ApiResponse(responseCode = "404", description = "User not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody @Valid UserDTO user, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        user.setId(id);
        var updatedUser = service.update(user, currentUserDetails);
        return ResponseEntity.ok().body(updatedUser);
    }
}
