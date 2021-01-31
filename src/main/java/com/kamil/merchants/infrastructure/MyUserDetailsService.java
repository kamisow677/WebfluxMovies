package com.kamil.merchants.infrastructure;

import com.kamil.merchants.infrastructure.repository.MyUser;
import com.kamil.merchants.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.security.Principal;


@Component
public class MyUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    UserRepository userRepository;

    public Mono<ServerResponse> register(ServerRequest request) {
        Mono<MyUser> myUserMono = request.bodyToMono(MyUser.class)
                .flatMap(user -> userRepository.findByUsername(user.getUsername())
                        .switchIfEmpty(userRepository.save(user)));
        return ServerResponse.ok().body(myUserMono, MyUser.class);
    }

    public  Mono<ServerResponse>  basicLogin(ServerRequest request) {
        Mono<MyUser> myUserMono = request.principal()
                .map(Principal::getName)
                .flatMap(it -> userRepository.findByUsername(it));

        return ServerResponse.ok().body(myUserMono, MyUser.class);
    }

    private Mono<UserDetails> toUserDetails(Mono<MyUser> userObject) {
        return userObject.map(it ->
                        User.withUsername(it.getUsername())
                                .password(it.getPassword())
                                .roles(it.getRole())
                                .build()
                );
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<MyUser> byUsername = userRepository.findByUsername(username);
        return toUserDetails(byUsername);

    }

}