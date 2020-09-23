package com.kamil.merchants.islands;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
public interface IslandRepository extends ReactiveCrudRepository<Island, String> {

    @Query("{ id: { $exists: true }}")
    Flux<Island> retrieveAllAuthors(final Pageable page);

    Mono<Island> findByName(String name);

}
