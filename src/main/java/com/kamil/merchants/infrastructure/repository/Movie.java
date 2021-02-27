package com.kamil.merchants.infrastructure.repository;

import lombok.Builder;
import lombok.ToString;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Builder
@ToString
public class Movie {

    @Id
    private String id;

    private String title;

    private String year;

    private byte[] image;

    private List<Upflix> upflixes;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image2) {
        this.image = image2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Upflix> getUpflixes() {
        return upflixes;
    }

    public void setUpflixes(List<Upflix> upflixes) {
        this.upflixes = upflixes;
    }

    public Movie(String id, String title, String year, byte[] image, List<Upflix> upflixes) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.image = image;
        this.upflixes = upflixes;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", upflixes=" + upflixes +
                '}';
    }
}
