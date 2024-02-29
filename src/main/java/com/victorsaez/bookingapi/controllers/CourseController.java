package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.CourseDTO;
import com.victorsaez.bookingapi.services.CourseService;
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
@Tag(name = "Courses")
@RequestMapping(value = "/v1/courses",  produces = "application/json")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all courses in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Sorting criteria", schema = @Schema(type = "string", defaultValue = "id,desc"))
    })
    public ResponseEntity<Page<CourseDTO>> findAll(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        return ResponseEntity.ok().body(service.findAll(pageable, currentUserDetails));
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns course by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Course not found.")
    })
    public ResponseEntity<CourseDTO> findById(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
           return ResponseEntity.ok().body(service.findById(id, currentUserDetails));
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete course by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Course not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        service.delete(id, currentUserDetails);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Create a new course in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<CourseDTO> insert(@RequestBody @Valid CourseDTO course, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        var createdCourse = service.insert(course, currentUserDetails);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdCourse.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdCourse);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a course in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully."),
            @ApiResponse(responseCode = "404", description = "Course not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @RequestBody @Valid CourseDTO course, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        course.setId(id);
        var updatedCourse = service.update(course, currentUserDetails);
        return ResponseEntity.ok().body(updatedCourse);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a course in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully."),
            @ApiResponse(responseCode = "404", description = "Course not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public ResponseEntity<CourseDTO> patch(@PathVariable Long id, @RequestBody CourseDTO courseDTO, @Parameter(hidden = true) @AuthenticationPrincipal UserDetails currentUserDetails) {
        CourseDTO updatedDto = service.patch(id, courseDTO, currentUserDetails);
        return ResponseEntity.ok(updatedDto);
    }
}
