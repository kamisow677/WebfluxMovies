package com.kamil.merchants.infrastructure;

import com.kamil.merchants.Generator;
import com.kamil.merchants.infrastructure.repository.MovieRepository;
import com.kamil.merchants.kafka.KafkaService;
import com.kamil.merchants.infrastructure.repository.Movie;
import com.kamil.merchants.infrastructure.parser.UpflixParser;
import com.kamil.merchants.infrastructure.repository.Upflix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ServiceResponseHandler {


    @Autowired
    UpflixParser upflixParser;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    Generator generator;


    public Mono<ServerResponse> getUpflixMovieData(ServerRequest request) {

        String filmName = request.queryParam("filmName").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "FileName is bad"));
        String filmYear = request.queryParam("filmYear").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "filmYear is bad"));

        List<Upflix> upflixes = upflixParser.getAllUpflixesFromWeb(filmName, filmYear);
        Mono<Movie> movieMono = save(generator.generateUUID(), filmName, filmYear, upflixes);

//        double dataForExtendedMovie = upflixParser.getExtendedDataMovie(filmName, filmYear);
        kafkaService.sendMovieToKafka(movieMono);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(movieMono, Upflix.class);
    }

    public Mono<ServerResponse> getAllMovies(ServerRequest request) {
        Flux<Movie> upflixFlux = movieRepository.getAllMovies();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }

    public Mono<ServerResponse> getAllUpflixes(ServerRequest request) {
        Flux<Object> upflixFlux = movieRepository.getAllDistinctUpflixes();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }

    public Mono<ServerResponse> getAllMoviesOnSiteName(ServerRequest request) {
        String siteName = request.pathVariable("siteName");
        Flux<Movie> upflixFlux = movieRepository.getAllMoviesOnSiteName(siteName);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }


    public Mono<ServerResponse> deleteMovieById(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Void> voidMono = movieRepository.deleteById(movieId);
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> deleteAllMovies(ServerRequest request) {
        Mono<Void> voidMono = movieRepository.deleteAll();
        return ServerResponse.ok().build(voidMono);
    }


    public Mono<ServerResponse> getMovieById(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Movie> upflixMono = movieRepository.getById(movieId)
                .switchIfEmpty(Mono.error(new Exception("No Movie  was found with id:  " + movieId)));
        return upflixMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> saveMovie(ServerRequest request) {
        Mono<Movie> movieMono = request.bodyToMono(Movie.class)
                .flatMap(movie -> save(generator.generateUUID(), movie.getTitle(), movie.getYear(), null));

        return movieMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<Movie> save(String id, String title, String year, List<Upflix> upflixList) {
        return movieRepository.findByTitle(title)
                .switchIfEmpty(movieRepository.save(
                        Movie.builder()
                                .id(id)
                                .title(title)
                                .year(year)
                                .upflixes(upflixList)
                                .build()
                        )
                );
    }


}
