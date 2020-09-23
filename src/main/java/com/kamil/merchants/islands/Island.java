package com.kamil.merchants.islands;

import com.fasterxml.jackson.annotation.JsonTypeId;
import com.kamil.merchants.heaven.Heaven;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class Island {

    @Id
    private String id;

    private String name;

    private List<Heaven> heavens;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Heaven> getHeavens() {
        return heavens;
    }

    public void setHeavens(List<Heaven> heavens) {
        this.heavens = heavens;
    }

    public Island() {
    }

    public Island(String id, String name, List<Heaven> heavens) {
        this.id = id;
        this.name = name;
        this.heavens = heavens;
    }
}
