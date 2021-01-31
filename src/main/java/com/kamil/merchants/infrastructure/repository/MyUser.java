package com.kamil.merchants.infrastructure.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyUser {

    @Id
    private String id;

    private String username;

    private String password;

    private String role;

}
