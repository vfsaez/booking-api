package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.StudentDTO;
import com.victorsaez.bookingapi.exceptions.AgeRequirementsException;
import com.victorsaez.bookingapi.services.StudentService;
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

@RestController
@Tag(name = "Students")
@RequestMapping(value = "/v1/students", produces = "application/json")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @Operation(summary = "Returns all students in database.")
    @ApiResponse(responseCode =  "200", description = "OK")
    @GetMapping
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<StudentDTO>> findAll(@Parameter(hidden = true) @PageableDefault(page = 0, size = 20, sort = "id,desc") Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns student by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "200", description = "OK."),
            @ApiResponse(responseCode =  "404", description = "Student not found.")
    })
    public ResponseEntity<StudentDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @PostMapping
    @Operation(summary = "Create a new student in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "Student created with success."),
            @ApiResponse(responseCode =  "400", description = "Invalid request.")
    })
    public ResponseEntity<StudentDTO> insert(@RequestBody @Valid StudentDTO student, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) throws AgeRequirementsException {
        var createdStudent = service.insert(student, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdStudent.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdStudent);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a student in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully."),
            @ApiResponse(responseCode = "404", description = "Student not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody @Valid StudentDTO student, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) throws AgeRequirementsException {
        student.setId(id);
        var updatedStudent = service.update(student, currentUserDetails);
        return ResponseEntity.ok().body(updatedStudent);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a student in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully."),
            @ApiResponse(responseCode = "404", description = "Student not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<StudentDTO> patch(@PathVariable Long id, @RequestBody StudentDTO studentDTO, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) throws AgeRequirementsException {
        StudentDTO updatedDto = service.patch(id, studentDTO, currentUserDetails);
        return ResponseEntity.ok(updatedDto);
    }
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete student by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "204", description = "Deleted with success."),
            @ApiResponse(responseCode =  "404", description = "Student not found."),
            @ApiResponse(responseCode =  "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }
}
