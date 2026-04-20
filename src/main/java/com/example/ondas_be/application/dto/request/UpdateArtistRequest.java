package com.example.ondas_be.application.dto.request;

import lombok.Data;

@Data
public class UpdateArtistRequest {

    private String name;
    private String slug;
    private String bio;
    private String country;
}
