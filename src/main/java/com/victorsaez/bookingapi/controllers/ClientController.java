package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.dto.ClientDTO;
import com.victorsaez.bookingapi.services.ClientService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Clients")
@RequestMapping(value = "/v1/clients", produces = "application/json")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @Operation(summary = "Returns all clients in database.")
    @ApiResponse(responseCode =  "200", description = "OK")
    @GetMapping
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<ClientDTO>> findAll(@Parameter(hidden = true) @PageableDefault(page = 0, size = 20, sort = "id,desc") Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns client by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "200", description = "OK."),
            @ApiResponse(responseCode =  "404", description = "Client not found.")
    })
    public ResponseEntity<ClientDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @PostMapping
    @Operation(summary = "Create a new client in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "Client created with success."),
            @ApiResponse(responseCode =  "400", description = "Invalid request.")
    })
    public ResponseEntity<ClientDTO> insert(@RequestBody @Valid ClientDTO client, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdClient = service.insert(client, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdClient.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdClient);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a client in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated successfully."),
            @ApiResponse(responseCode = "404", description = "Client not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<ClientDTO> update(@PathVariable Long id, @RequestBody @Valid ClientDTO client, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        client.setId(id);
        var updatedClient = service.update(client, currentUserDetails);
        return ResponseEntity.ok().body(updatedClient);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete client by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "204", description = "Deleted with success."),
            @ApiResponse(responseCode =  "404", description = "Client not found."),
            @ApiResponse(responseCode =  "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }
}
