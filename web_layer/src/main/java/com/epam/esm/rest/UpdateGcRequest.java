package com.epam.esm.rest;

import com.epam.esm.dto.TagDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Class is used as request body when updating gift certificate.
 *
 * @author Danylo Proshyn
 */

@Getter
@Setter
public class UpdateGcRequest {

    @NotNull
    private Long    id;
    private String  name;
    private String  description;
    private Double  price;
    private Integer duration;

    private List<TagDTO> tags;
}
