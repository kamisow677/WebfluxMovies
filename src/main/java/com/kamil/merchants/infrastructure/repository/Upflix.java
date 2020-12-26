package com.kamil.merchants.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Upflix {

    private String siteName;

    private String link;

    private String distributionChoice;

    public Upflix(Upflix upflix) {
        siteName = upflix.siteName;
        link = upflix.link;
        distributionChoice = upflix.distributionChoice;

    }
}
