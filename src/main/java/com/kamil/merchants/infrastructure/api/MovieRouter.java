package com.kamil.merchants.infrastructure.api;

import com.kamil.merchants.infrastructure.ServiceResponseHandler;
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
    public RouterFunction<ServerResponse> routeMovie(ServiceResponseHandler handler) {

        return RouterFunctions
                .route(RequestPredicates.DELETE("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::deleteMovieById)
                .andRoute(RequestPredicates.GET("/movie/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getMovieById)
                .andRoute(RequestPredicates.POST("/movie")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::saveMovie);
    }

    @Bean
    public RouterFunction<ServerResponse> asd(ServiceResponseHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/movie/sitename/{siteName}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getAllMoviesOnSiteName);
    }

//    @Bean
//    public  RouterFunction<ServerResponse> feign(FeignService feignService) {
//        return RouterFunctions
//                .route(RequestPredicates.GET("/feign/")
//                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), feignService::feignMethod);
//    }


    @Bean
    public RouterFunction<ServerResponse> routeMovieAll(ServiceResponseHandler handler) {

        return RouterFunctions
                .route(RequestPredicates.GET("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::getAllMovies)
                .andRoute(RequestPredicates.DELETE("/movieAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::deleteAllMovies);

    }

}
