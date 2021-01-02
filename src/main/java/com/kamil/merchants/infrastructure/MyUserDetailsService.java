package com.kamil.merchants.infrastructure;

import com.kamil.merchants.infrastructure.repository.Movie;
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

import java.util.*;

@Component
public class MyUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    UserRepository userRepository;

    private static List<MyUserDetailsService.UserObject> users = new ArrayList();

    public MyUserDetailsService() {
        users.add(new MyUserDetailsService.UserObject("admin1", "{noop}admin1", "ADMIN"));
        users.add(new MyUserDetailsService.UserObject("admin", "{noop}admin", "ADMIN"));
        users.add(new MyUserDetailsService.UserObject("user1", "{noop}user1", "USER"));
    }

    public Mono<ServerResponse> register(ServerRequest request) {
        Mono<MyUser> myUserMono = request.bodyToMono(MyUser.class)
                .flatMap(user -> userRepository.findByUsername(user.getUsername())
                        .switchIfEmpty(userRepository.save(user)));
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

    private UserDetails toUserDetails(UserObject userObject) {
        return User.withUsername(userObject.name)
                .password(userObject.password)
                .roles(userObject.role).build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<MyUser> byUsername = userRepository.findByUsername(username);
        return toUserDetails(byUsername);
//
//        Optional<MyUserDetailsService.UserObject> any = users.stream()
//                .filter(u -> u.name.equals(username))
//                .findAny();
//        return Mono.just(toUserDetails(any.get()));

    }

    private static class UserObject {
        private String name;
        private String password;
        private String role;

        public UserObject(String name, String password, String role) {
            this.name = name;
            this.password = password;
            this.role = role;
        }
    }
}