package com.kamil.merchants.movie;

import com.kamil.merchants.upflix.Upflix;
import com.kamil.merchants.upflix.UpflixService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@Slf4j
public class MovieService {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UpflixService upflixService;

    public Mono<ServerResponse> delete(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Void> voidMono = movieRepository.deleteById(movieId);
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> deleteAll(ServerRequest request) {
        Mono<Void> voidMono = movieRepository.deleteAll();
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Movie> movieMono = movieRepository.findById(movieId)
                .switchIfEmpty(Mono.error(new Exception("No movie  was found with id:  "+ movieId)));
        return movieMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<Movie> movieFlux = movieRepository.findAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(movieFlux, Movie.class);
    }

    public Mono<ServerResponse> getAllMoviesFromUpflixSite(ServerRequest request) {
        String siteName = request.pathVariable("siteName");
        Flux<Upflix> upflixesBySiteName = upflixService.findBySiteName(siteName);
        Flux<Movie> objectFlux = upflixesBySiteName.flatMap(upflix -> movieRepository.findById(upflix.getMovieId()));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(objectFlux, Movie.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        log.info("WSZEDLEM");
        Mono<Movie> movieMono = request.bodyToMono(Movie.class)
                .flatMap(movie -> {
                    return save(UUID.randomUUID(), movie.getTitle(), movie.getYear());
                });

        return movieMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<Movie> save(UUID id, String title, String year) {
        return movieRepository.findByTitle(title)
                .switchIfEmpty(movieRepository.save(
                        Movie.builder()
                                .id(id.toString())
                                .title(title)
                                .year(year)
                                .build()
                        )
                );
    }
}