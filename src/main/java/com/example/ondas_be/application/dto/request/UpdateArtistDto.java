package com.example.ondas_be.application.dto.request;

public class UpdateArtistDto {

    private String name;
    private String slug;
    private String bio;
    private String country;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
