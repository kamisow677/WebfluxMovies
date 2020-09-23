package com.kamil.merchants.upflix;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Upflix {

    @Id
    private String id;

    private String movie_id;

    private String siteName;

    private String link;

    private String distributionChoice;

}
