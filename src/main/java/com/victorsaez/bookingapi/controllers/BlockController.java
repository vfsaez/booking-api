package com.victorsaez.bookingapi.controllers;

import com.victorsaez.bookingapi.dto.BlockDTO;
import com.victorsaez.bookingapi.services.BlockService;
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
@Tag(name = "Blocks")
@RequestMapping(value = "/blocks", produces = "application/json")
public class BlockController {

    private final BlockService service;



    public BlockController(BlockService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Returns all blocks in database.")
    @ApiResponse(responseCode = "200", description = "OK.")
    public ResponseEntity<List<BlockDTO>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "Returns block by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "404", description = "Block not found.")
    })
    public ResponseEntity<BlockDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new block in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Block created with success."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BlockDTO> insert (@RequestBody @Valid BlockDTO block) {
        var createdBlock = service.insert(block);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(createdBlock.getId())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(createdBlock);
    }

    @PutMapping(value = "/{id}")
    @Operation(summary = "Update a block in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block updated successfully."),
            @ApiResponse(responseCode = "404", description = "Block not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "400", description = "Property \"name\" Id: 4  - not available trough given dates: Thu Dec 07 18:24:31 BRT 2023 - Thu Dec 07 18:24:31 BRT 2023")
    })
    public ResponseEntity<BlockDTO> update(@PathVariable Long id, @RequestBody @Valid BlockDTO block) {
        block.setId(id);
        var updatedBlock = service.update(block);
        return ResponseEntity.ok().body(updatedBlock);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete block by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted with success."),
            @ApiResponse(responseCode = "404", description = "Block not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request.")
    })
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}