//package com.kamil.merchants.feign;
//
//import com.kamil.merchants.movie.Movie;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Service
//public class FeignService {
//
//    @Autowired
//    FeignMovies feignMovies;
//
//    public Mono<ServerResponse> feignMethod(ServerRequest request){
//        Flux<Iterable<MovieFeign>> just = Flux.just(feignMovies.getAll().getBody());
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(just, Movie.class);
//    }
//
//}
