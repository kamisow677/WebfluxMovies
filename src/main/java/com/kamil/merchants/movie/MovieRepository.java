package com.kamil.merchants.movie;

import com.kamil.merchants.upflix.Upflix;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
public interface MovieRepository extends ReactiveCrudRepository<Movie, String> {

    Mono<Movie> findByTitle(String title);
}
