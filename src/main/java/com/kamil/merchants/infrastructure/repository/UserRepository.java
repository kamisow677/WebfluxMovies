package com.kamil.merchants.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
public interface UserRepository extends ReactiveCrudRepository<MyUser, String> {

    Mono<MyUser> findByUsername(String username);
}