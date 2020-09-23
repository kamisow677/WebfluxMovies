package com.kamil.merchants.upflix;

import com.kamil.merchants.movie.Movie;
import com.kamil.merchants.movie.MovieService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class UpflixService {

    @Autowired
    UpflixRepository upflixRepository;

    @Autowired
    UpflixParser upflixParser;

    @Autowired
    MovieService movieService;

    public Mono<ServerResponse> getUpflixMovieData(ServerRequest request) {

        Optional<String> filmName = request.queryParam("filmName");
        Optional<String> filmYear = request.queryParam("filmYear");

        Mono<Movie> movieMono = movieService.save(filmName.get(), filmYear.get());


        List<Upflix> allUpflixesFromWeb = upflixParser.getAllUpflixesFromWeb(filmName.get(), filmYear.get());

        Flux<Upflix> objectFlux = Flux.fromIterable(allUpflixesFromWeb)
                .flatMap(upflix -> {
                    Mono<Upflix> savedUpflix = movieMono.flatMap(movie -> {
                        upflix.setMovie_id(movie.getId());
                        return save(upflix);
                    });
                    return savedUpflix;
                });

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(objectFlux, Movie.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Flux<Upflix> upflixFlux = getAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Movie.class);
    }

    public Flux<Upflix> getAll() {
        return upflixRepository.findAll();
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String upflixId = request.pathVariable("id");
        Mono<Void> voidMono = upflixRepository.deleteById(upflixId);
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> deleteAll(ServerRequest request) {
        Mono<Void> voidMono = upflixRepository.deleteAll();
        return ServerResponse.ok().build(voidMono);
    }


    private Mono<Upflix> save(Upflix upflix) {
        Mono<Upflix> upflixMono = upflixRepository.findBySiteNameAndDistributionChoice(upflix.getSiteName(), upflix.getDistributionChoice())
                .switchIfEmpty(upflixRepository.save(upflix));
        return upflixMono;
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String upflixId = request.pathVariable("id");
        Mono<Upflix> islandMono = getById(upflixId)
                .switchIfEmpty(Mono.error(new Exception("No Upflix  was found with id:  " + upflixId)));
        return islandMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<Upflix> getById(String upflixId) {
        return upflixRepository.findById(upflixId);
    }

    public Flux<Upflix> findBySiteName(String siteName) {
        return upflixRepository.findBySiteName(siteName);
    }
}

