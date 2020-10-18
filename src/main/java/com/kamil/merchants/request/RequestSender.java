package com.kamil.merchants.request;

import com.kamil.merchants.movie.Movie;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RequestSender {

    public Mono<Movie> saveMovie(String filmName, String filmYear) {
        WebClient client = WebClient.create("http://localhost:8080");
        WebClient.UriSpec<WebClient.RequestBodySpec> request1 = client.method(HttpMethod.POST);

        return client.post()
                .uri("/movie")
                .bodyValue(Movie.builder().title(filmName).year(filmYear).build())
                .exchange()
                .flatMap(response -> response.bodyToMono(Movie.class));
    }
}
