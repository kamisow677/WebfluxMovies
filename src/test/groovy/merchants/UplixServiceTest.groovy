package com.kamil.merchants


import com.kamil.merchants.upflix.UpflixRepository
import com.kamil.merchants.upflix.UpflixRouter
import com.kamil.merchants.upflix.UpflixService
import groovy.json.JsonSlurper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunction
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import static merchants.UpflixTestBilder.createUpflixDistrChoice
import static org.mockito.Mockito.when

@SpringBootTest
class UplixServiceTest extends Specification {

    @Autowired
    UpflixService upflixService

    @MockBean
    UpflixRepository upflixRepository

    WebTestClient client

    @BeforeEach
    void setup() {
//        upflixRepository =  Mockito.mock(UpflixRepository.class)
        RouterFunction<?> route = (new UpflixRouter()).routeUpflixAll(upflixService)
        client = WebTestClient.bindToRouterFunction(route).build()
    }

    @Test
    void getAllUpoflxies() {
        def distrChoice = ["oplata", "abo"]
        def upflixes = []
        distrChoice.forEach { it ->
            upflixes.add(createUpflixDistrChoice(it).build())
        }
        when(upflixRepository.findAll()).thenReturn(Flux.just(upflixes));

        def eventFlux = client.get()
                .uri("/upflixAll")
                .exchange()
                .expectStatus().isOk()
                .expectBody()

        def jsonslurper = new JsonSlurper()
        def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)
        parse.toString()
        assert parse[0][0].link == upflixes[0].link
        assert parse[0][1].link == upflixes[1].link

	}

}

