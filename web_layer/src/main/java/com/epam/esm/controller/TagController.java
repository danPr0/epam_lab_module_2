package com.epam.esm.controller;

import com.epam.esm.dto.TagDTO;
import com.epam.esm.rest.AddTagRequest;
import com.epam.esm.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller class responsible for operations with tags.
 *
 * @author Danylo Proshyn
 */

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    private final String    resourceCode = "02";

    @Autowired
    public TagController(TagService tagService) {

        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTag(@PathVariable long id) {

        return tagService.getTag(id).<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(
                () -> ResponseEntity.status(404)
                        .body(Map.of("errorMessage", String.format("Requested resource not found (id = %s)", id),
                                "errorCode", "404" + resourceCode)));
    }

    @PostMapping()
    public ResponseEntity<Object> addTag(@Valid @RequestBody AddTagRequest req) {

        if (tagService.addTag(TagDTO.builder().id(req.getId()).name(req.getName()).build())) {
            return ResponseEntity.status(201).build();
        } else {
            return ResponseEntity.status(409)
                    .body(Map.of("errorMessage", String.format("Resource already exists (id = %s)", req.getId()),
                            "errorCode", "409" + resourceCode));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTag(@PathVariable long id) {

        if (tagService.deleteTag(id)) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("errorMessage", String.format("Requested resource not found (id = %s)", id),
                            "errorCode", "404" + resourceCode));
        }
    }
}
