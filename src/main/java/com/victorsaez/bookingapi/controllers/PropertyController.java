package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.PropertyDTO;
import com.victorsaez.bookingapi.services.PropertyService;
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
@Tag(name = "Properties")
@RequestMapping(value = "/properties",  produces = "application/json")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all properties in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    public ResponseEntity<List<PropertyDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns property by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Property not found.")
    })
    public ResponseEntity<PropertyDTO> findById(@PathVariable Long id) {
           return ResponseEntity.ok().body(service.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete property by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Property not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping
    @Operation(summary = "Create a new property in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Property created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<PropertyDTO> insert(@RequestBody @Valid PropertyDTO property) {
        var createdProperty = service.insert(property);
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
    public ResponseEntity<PropertyDTO> update(@PathVariable Long id, @RequestBody @Valid PropertyDTO property) {
        property.setId(id);
        var updatedProperty = service.update(property);
        return ResponseEntity.ok().body(updatedProperty);
    }
}
