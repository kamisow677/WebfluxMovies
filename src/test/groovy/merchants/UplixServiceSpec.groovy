package com.kamil.merchants

import com.kamil.merchants.upflix.Upflix
import javafx.util.Pair
import merchants.BaseIntegration
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Unroll

import static merchants.UpflixTestBilder.createUpflixDistrChoice
import static merchants.UpflixTestBilder.createUpflix
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when

@SpringBootTest
class UplixServiceSpec extends BaseIntegration {

    @Shared
    Upflix dummy = createUpflixDistrChoice("nament").build()

    def "when context is loaded then all expected beans are created"() {
        expect: "the context is created"
            upflixService
    }

    def cleanupper() {
        upflixRepository.deleteAll().block()
    }

    def "get all upflixes"() {
        given:
            cleanupper()
            def distrChoice = ["oplata", "abo"]
            def upflixes = []
            upflixes.add(upflixRepository.save(createUpflix(distrChoice[0], "1").build()).block())
            upflixes.add(upflixRepository.save(createUpflix(distrChoice[1], "2").build()).block())
        when:
            def eventFlux = createGetAllRequest("/upflixAll")
        then:
            def parse = jsonslurpe.parse(eventFlux.returnResult().responseBody)
            parse.toString()
            assert parse[0].link == upflixes[0].link
            assert parse[1].link == upflixes[1].link

    }

    def "get upflixes by id"() {
        given:
            cleanupper()
            def dum = createUpflix("TestowyChoice", "3")
        when:
            upflixRepository.save(dum.build()).block()
            def eventFlux = createGetRequest("/upflix/3")
        then:
            def parse = jsonslurpe.parse(eventFlux.returnResult().responseBody)
            assert parse.link == dum.link
            assert parse.distributionChoice == dum.distributionChoice

    }

    def "delete upflixes"() {
        given:
            cleanupper()
            def dummy = createUpflixDistrChoice("Abonament").build()
            upflixRepository.save(dummy).block()
        when:
            def eventFlux = createDaleteRequest("/upflix/1")
        then:
            noExceptionThrown()
            assert null == eventFlux.returnResult().responseBody
    }

    @Unroll
    def "save upflix #dummyExistsInDB"() {
        given:
            cleanupper()
            if (dummyExistsInDB == true)
                upflixRepository.save(expected).block()
        when:
            def eventFlux = upflixService.save(dummy)
        then:
            StepVerifier.create(eventFlux)
                    .expectNext(expected)
                    .verifyComplete()
            StepVerifier.create(upflixRepository.findById(dummy.getId()))
                    .expectNextCount(1)
                    .verifyComplete()
        where:
            dummyExistsInDB || expected
            true      || dummy
            false     || dummy
    }

    def "getUpflixMovieData"() {
        given:
            cleanupper()
            def dum = createUpflix("TestowyChoice", "4")
            def dum2 = createUpflix("TestowyChoice", "5")
            upflixRepository.save(dum.build()).block()
            upflixRepository.save(dum2.build()).block()
            ArrayList arrayList = new ArrayList()
            arrayList.add(new Pair("filmName", "filmName"))
            arrayList.add(new Pair("filmYear", "filmYear"))
        when(upflixParser.getAllUpflixesFromWeb(anyString(), anyString()))
                .thenReturn(Arrays.asList(createUpflix("distChoice", "6").build()))
        when:
            def eventFlux = createGetRequest("/upflix", arrayList)
        then:
            def parse = jsonslurpe.parse(eventFlux.returnResult().responseBody)
            assert parse.link == dum.link
            assert parse.distributionChoice == dum.distributionChoice

    }

}

