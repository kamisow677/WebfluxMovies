package com.kamil.merchants.infrastructure.api;

import com.kamil.merchants.infrastructure.ServiceResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@CrossOrigin
public class MovieRouter {

    @Bean
    public RouterFunction<ServerResponse> routeMovie(ServiceResponseHandler handler) {

        return RouterFunctions
                .route(RequestPredicates.DELETE("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::deleteMovieById)
                .andRoute(RequestPredicates.GET("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getMovieById)
                .andRoute(RequestPredicates.PUT("/movie/upflix")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::updateUpflixlink)
                .andRoute(RequestPredicates.POST("/movie")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::saveMovie)
                .andRoute(RequestPredicates.POST("/movie/image")
                        .and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA)), handler::uploadImage)
                .andRoute(RequestPredicates.GET("/movie/image/pla")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getImage);
    }

    @Bean
    public RouterFunction<ServerResponse> routeMovieExtra(ServiceResponseHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/movie/sitename/{siteName}")
                    .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getAllMoviesOnSiteName)
                .andRoute(RequestPredicates.GET("/movie/best/best")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getBest);
    }


    @Bean
    public RouterFunction<ServerResponse> routeMovieAll(ServiceResponseHandler handler) {

        return RouterFunctions
                .route(RequestPredicates.GET("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getAllMovies)
                .andRoute(RequestPredicates.DELETE("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::deleteAllMovies);

    }

}

