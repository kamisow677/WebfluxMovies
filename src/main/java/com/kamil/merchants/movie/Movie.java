package com.kamil.merchants.movie;

import com.kamil.merchants.upflix.Upflix;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Builder
public class Movie {

    @Id
    private String id;

    private String title;

    private String year;

    private List<Upflix> upflixes;

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

    public Movie(String id, String title, String year, List<Upflix> upflixes) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.upflixes = upflixes;
    }

    public Movie() {
    }
}
