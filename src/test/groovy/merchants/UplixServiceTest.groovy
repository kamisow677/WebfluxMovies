package com.kamil.merchants

import com.kamil.merchants.upflix.Upflix
import com.kamil.merchants.upflix.UpflixRepository
import com.kamil.merchants.upflix.UpflixRouter
import com.kamil.merchants.upflix.UpflixService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunction
import reactor.core.publisher.Mono
import spock.lang.Specification

import static merchants.UpflixTestBilder.*

@SpringBootTest
class UplixServiceTest extends Specification {

    @Autowired
    UpflixService upflixService

    @Autowired
    UpflixRepository upflixRepository

    WebTestClient client

    @BeforeEach
    void setup() {
        upflixRepository.deleteAll()
        RouterFunction<?> route = (new UpflixRouter()).routeUpflixAll(upflixService)
        client = WebTestClient.bindToRouterFunction(route).build()
    }

    @Test
    void getAllUpoflxies() {
        def upflixes = ["oplata", "abo"]
        upflixes.forEach {it -> upflixRepository.save(
                createUpflixDistrChoice(it).build()
        )}

        def list = client.get().uri("/upflixAll")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Upflix.class)

        def body = list.returnResult().responseBody;
        def collection = body.collect { it.distributionChoice }
        assert body.size() == 2
        assert upflixes.containsAll(collection)
	}

}

