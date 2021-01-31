package com.kamil.merchants.infrastructure.api;

import com.kamil.merchants.infrastructure.MyUserDetailsService;
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
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUser(MyUserDetailsService handler) {

        return RouterFunctions
                .route(RequestPredicates.POST("/user/register")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::register)
                .andRoute(RequestPredicates.GET("/user/login")
                        .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::basicLogin);
    }

}

