package com.kamil.merchants.movie;

import com.kamil.merchants.upflix.UpflixService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MovieRouter {

    @Bean
    public RouterFunction<ServerResponse> routeMovie(MovieService movieService) {

        return RouterFunctions
                .route(RequestPredicates.DELETE("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), movieService::delete)
                .andRoute(RequestPredicates.GET("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), movieService::getById)
                .andRoute(RequestPredicates.POST("/movie")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), movieService::save);
    }

    @Bean
    public RouterFunction<ServerResponse> asd(MovieService movieService) {
        return RouterFunctions
                .route(RequestPredicates.GET("/movie/sitename/{siteName}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), movieService::getAllMoviesFromUpflixSite);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMovieAll(MovieService movieService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), movieService::getAll)
                .andRoute(RequestPredicates.DELETE("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), movieService::deleteAll);

    }

}

