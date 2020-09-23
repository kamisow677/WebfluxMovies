package com.kamil.merchants.upflix;

import com.kamil.merchants.islands.Island;
import com.kamil.merchants.movie.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
public interface UpflixRepository extends ReactiveCrudRepository<Upflix, String> {
    Mono<Upflix> findBySiteNameAndDistributionChoice(String siteName, String distributionChoice);
    Flux<Upflix> findBySiteName(String siteName);
}
