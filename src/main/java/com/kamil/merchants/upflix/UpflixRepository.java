package com.kamil.merchants.upflix;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
public interface UpflixRepository extends ReactiveCrudRepository<Upflix, String> {
    Mono<Upflix> findBySiteNameAndDistributionChoiceAndMovieId(String siteName, String distributionChoice, String movie_id);
    Flux<Upflix> findBySiteName(String siteName);
}
