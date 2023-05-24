package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDTO;
import com.epam.esm.rest.AddGcRequest;
import com.epam.esm.rest.UpdateGcRequest;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.util_service.Order;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller class responsible for operations with gift certificates.
 *
 * @author Danylo Proshyn
 */

@RestController
@RequestMapping("/gift-certificates")
public class GiftCertificateController {

    private final GiftCertificateService gcService;

    private final String    resourceCode = "01";
    private final Validator validator    = Validation.buildDefaultValidatorFactory().getValidator();
    private final Logger    logger       = LogManager.getLogger(GiftCertificateController.class);

    @Autowired
    public GiftCertificateController(GiftCertificateService gcService) {

        this.gcService = gcService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGiftCertificate(@PathVariable long id) {

        return gcService.getGiftCertificate(id).<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(
                () -> ResponseEntity.status(404)
                        .body(Map.of("errorMessage", String.format("Requested resource not found (id = %s)", id),
                                "errorCode", "404" + resourceCode)));
    }

    @GetMapping
    public ResponseEntity<List<GiftCertificateDTO>> getAll(
            @RequestParam(required = false) Optional<String> tagName,
            @RequestParam(required = false) Optional<String> namePart,
            @RequestParam(required = false) Optional<String> descriptionPart,
            @RequestParam(required = false) Optional<List<String>> sort) {

        Optional<Order> nameOrder       = Optional.empty();
        Optional<Order> createDateOrder = Optional.empty();

        if (sort.isPresent()) {

            if (sort.get().contains("name_asc")) {
                nameOrder = Optional.of(Order.asc);
            } else if (sort.get().contains("name_desc")) {
                nameOrder = Optional.of(Order.desc);
            } else {
                nameOrder = Optional.empty();
            }

            if (sort.get().contains("createDate_asc")) {
                createDateOrder = Optional.of(Order.asc);
            } else if (sort.get().contains("createDate_desc")) {
                createDateOrder = Optional.of(Order.desc);
            } else {
                createDateOrder = Optional.empty();
            }
        }

        return ResponseEntity.ok(gcService.getAll(tagName, namePart, descriptionPart, nameOrder, createDateOrder));
    }

    @PostMapping
    public ResponseEntity<Object> addGiftCertificate(@RequestBody AddGcRequest req) {

        if (validator.validate(req).size() > 0) {
            throw new ValidationException();
        }

        if (!gcService.addGiftCertificate(
                    GiftCertificateDTO.builder().id(req.getId()).name(req.getName()).description(req.getDescription())
                            .price(req.getPrice()).duration(req.getDuration()).tags(req.getTags()).build())) {
            String errorMsg = String.format("Resource already exists (id " + "= %s)", req.getId());
            logger.error(errorMsg);

            return ResponseEntity.status(409).body(Map.of("errorMessage", errorMsg, "errorCode", "409" + resourceCode));
        }

        return ResponseEntity.status(201).build();
    }

    @PutMapping()
    public ResponseEntity<Object> updateGiftCertificate(@RequestBody UpdateGcRequest req) {

        if (validator.validate(req).size() > 0) {
            throw new ValidationException();
        }

        if (!gcService.updateGiftCertificate(
                    GiftCertificateDTO.builder().id(req.getId()).name(req.getName()).description(req.getDescription())
                            .price(req.getPrice()).duration(req.getDuration()).tags(req.getTags()).build())) {
            String errorMsg = String.format("Resource not found (id = %s)", req.getId());
            logger.error(errorMsg);

            return ResponseEntity.status(404).body(Map.of("errorMessage", errorMsg, "errorCode", "404" + resourceCode));
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGiftCertificate(@PathVariable long id) {

        if (!gcService.deleteGiftCertificate(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("errorMessage", String.format("Requested resource not " + "found (id = %s)", id),
                            "errorCode", "404" + resourceCode));
        }

        return ResponseEntity.status(204).build();
    }
}