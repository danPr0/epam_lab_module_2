package com.epam.esm.rest;


import com.epam.esm.dto.TagDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Class is used as request body when adding gift certificate.
 *
 * @author Danylo Proshyn
 */

@Getter
@Setter
public class AddGcRequest {

    @NotNull
    private Long    id;
    @NotNull
    private String  name;
    @NotNull
    private String  description;
    @NotNull
    private Double  price;
    @NotNull
    private Integer duration;

    private List<TagDTO> tags;
}
