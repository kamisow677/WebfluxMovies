package com.kamil.merchants.upflix;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration

//@AutoConfigureMockMvc
@Component

public class UpflixRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUpflix(UpflixService upflixService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/upflix")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::getUpflixMovieData)
                .andRoute(RequestPredicates.DELETE("/upflix/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::delete)
                .andRoute(RequestPredicates.GET("/upflix/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::getById);
    }


    @Bean
    public RouterFunction<ServerResponse> routeUpflixAll(UpflixService upflixService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/upflixAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::getAll)
                .andRoute(RequestPredicates.DELETE("/upflixAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::deleteAll);

    }


}

