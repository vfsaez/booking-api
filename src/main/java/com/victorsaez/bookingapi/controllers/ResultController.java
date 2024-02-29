package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.ResultDTO;
import com.victorsaez.bookingapi.services.ResultService;
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

@RestController
@Tag(name = "Results")
@RequestMapping(value = "/v1/results", produces = "application/json")
public class ResultController {

    private final ResultService service;

    public ResultController(ResultService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all results in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<ResultDTO>> findAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
         return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns result by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Result not found.")
    })
    public ResponseEntity<ResultDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @PostMapping
    @Operation(summary = "Create a new result in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Result created with success."),
            @ApiResponse(responseCode = "400", description = "Course \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<ResultDTO> insert(@RequestBody @Valid ResultDTO result, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdResult = service.insert(result, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdResult.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdResult);
    }

    @PatchMapping(value = "/{id}")
    @Operation(summary = "Update a result in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result updated successfully."),
            @ApiResponse(responseCode = "404", description = "Result not found."),
            @ApiResponse(responseCode = "400", description = "Course \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<ResultDTO> patch(@PathVariable Long id, @RequestBody ResultDTO resultDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        resultDto.setId(id);
        ResultDTO updatedDto = service.patch(id, resultDto, currentUserDetails);
        return ResponseEntity.ok(updatedDto);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a result in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result updated successfully."),
            @ApiResponse(responseCode = "404", description = "Result not found."),
            @ApiResponse(responseCode = "400", description = "Course \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<ResultDTO> update(@PathVariable Long id, @RequestBody @Valid ResultDTO resultDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        resultDto.setId(id);
        var updatedResult = service.update(resultDto, currentUserDetails);
        return ResponseEntity.ok().body(updatedResult);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete result by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Result not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }
}