package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.services.PropertyService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;


@RestController
@Tag(name = "Properties")
@RequestMapping(value = "/v1/properties",  produces = "application/json")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all properties in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<PropertyDTO>> findAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns property by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Property not found.")
    })
    public ResponseEntity<PropertyDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
           return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete property by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Property not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Create a new property in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Property created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<PropertyDTO> insert(@RequestBody @Valid PropertyDTO property, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdProperty = service.insert(property, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdProperty.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdProperty);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a property in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated successfully."),
            @ApiResponse(responseCode = "404", description = "Property not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<PropertyDTO> update(@PathVariable Long id, @RequestBody @Valid PropertyDTO property, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        property.setId(id);
        var updatedProperty = service.update(property, currentUserDetails);
        return ResponseEntity.ok().body(updatedProperty);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a property in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated successfully."),
            @ApiResponse(responseCode = "404", description = "Property not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<PropertyDTO> patch(@PathVariable Long id, @RequestBody PropertyDTO propertyDTO, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        PropertyDTO updatedDto = service.patch(id, propertyDTO, currentUserDetails);
        return ResponseEntity.ok(updatedDto);
    }
}
