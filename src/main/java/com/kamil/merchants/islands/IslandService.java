package com.kamil.merchants.islands;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Log4j2
public class IslandService {

    @Autowired
    IslandRepository islandRepository;

    public Mono<ServerResponse> delete(ServerRequest request) {
        String islandId = request.pathVariable("id");
        Mono<Void> voidMono = islandRepository.deleteById(islandId);
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> deleteAll(ServerRequest request) {
        Mono<Void> voidMono = islandRepository.deleteAll();
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String islandId = request.pathVariable("id");
        Mono<Island> islandMono = islandRepository.findById(islandId)
                .switchIfEmpty(Mono.error(new Exception("No island  was found with id:  "+ islandId)));
        return islandMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getByName(ServerRequest request) {
        Optional<String> islandName = request.queryParam("name");
        Mono<Island> islandMono = islandName.map( name -> islandRepository.findByName(name))
                .orElse(Mono.error(new Exception("No island  was found with name:  "+ islandName)));
        return islandMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        Optional<String> limit = request.queryParam("limit");
        Optional<String> offset = request.queryParam("offset");
        Flux<Island> islandFlux1 = offset.map(off ->
                limit.map(l -> islandRepository.retrieveAllAuthors(PageRequest.of(Integer.valueOf(off), Integer.valueOf(l))))
                        .orElseGet(() ->islandRepository.retrieveAllAuthors(PageRequest.of(Integer.valueOf(off), 10)))
        ).orElseGet(() -> islandRepository.findAll());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(islandFlux1, Island.class);
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Island> islandMono = save(request.bodyToMono(Island.class));
        return islandMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<Island> save(Mono<Island> mono) {
        return mono.flatMap(island -> islandRepository.save(island));
    }


    public  Mono<ServerResponse> update(Island toUpdateIsland, Island island) {
        Mono<Island> islandMono = islandRepository.findById(toUpdateIsland.getId());
        toUpdateIsland.setName(island.getName());
        toUpdateIsland.setHeavens(island.getHeavens());
        final Island block = islandMono.block();
        Mono<Island> save = islandRepository.save(block);

        return save.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }
}