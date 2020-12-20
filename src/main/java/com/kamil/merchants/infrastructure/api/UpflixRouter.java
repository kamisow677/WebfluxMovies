package com.kamil.merchants.infrastructure.api;

import com.kamil.merchants.infrastructure.ServiceResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Component
public class UpflixRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUpflix(ServiceResponseHandler upflixService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/upflix")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::getUpflixMovieData);
    }

    @Bean
    public RouterFunction<ServerResponse> routeUpflixes(ServiceResponseHandler upflixService) {

        return RouterFunctions
                .route(RequestPredicates.GET("/upflixAll")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), upflixService::getAllUpflixes);

    }


}

