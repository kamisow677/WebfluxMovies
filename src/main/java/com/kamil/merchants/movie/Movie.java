package com.kamil.merchants.movie;

import com.kamil.merchants.upflix.Upflix;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class Movie {

    @Id
    private String id;

    private String title;

    private String year;

    private List<Upflix> upflixes;

}
