package com.kamil.merchants.infrastructure.repository;

import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@ToString
public class UpflixWithCount extends Upflix {

    private Integer count;

    public UpflixWithCount(Upflix upflix) {
        super(upflix);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
