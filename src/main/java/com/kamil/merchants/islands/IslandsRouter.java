package com.kamil.merchants.islands;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class IslandsRouter {

    @Bean
    public RouterFunction<ServerResponse> route(IslandService islandService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/islands/{id}")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::getById)
                .andRoute(RequestPredicates.GET("/islands")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::getByName)
                .andRoute(RequestPredicates.POST("/islands")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), islandService::save)
                .andRoute(RequestPredicates.DELETE("/islands")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::delete);
//                .andRoute(RequestPredicates.PUT("/islands")
//                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::update);

    }

    @Bean
    public RouterFunction<ServerResponse> route2(IslandService islandService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/islandsAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::getAll)
                .andRoute(RequestPredicates.DELETE("/islandsAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), islandService::deleteAll);

    }

}

