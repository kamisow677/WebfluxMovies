package com.kamil.merchants


import com.kamil.merchants.upflix.Upflix
import com.kamil.merchants.upflix.UpflixParser
import javafx.util.Pair
import utils.BaseIntegration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Unroll

import static utils.UpflixTestBilder.createUpflixDistrChoice
import static utils.UpflixTestBilder.createUpflix
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when

@SpringBootTest
class UplixServiceSpec extends BaseIntegration {

    @MockBean
    UpflixParser upflixParser

    public static final String FILMNAME_KEY = "filmName"
    public static final String FILMNAME_VALUE = "filmName_value"
    public static final String FILMYEAR_KEY = "filmYear"
    public static final String FILMYEAR_VALUE = "filmYear_value"
    public static final String DIST_CHOICE = "distChoice"
    public static final String ID = "1"

    @Shared
    Upflix dummy = createUpflixDistrChoice("nament").build()

    def "when context is loaded then all expected beans are created"() {
        expect: "the context is created"
            upflixService
    }

    def cleanupper() {
        upflixRepository.deleteAll().block()
        movieRepository.deleteAll().block()
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
            ArrayList arrayList = new ArrayList()
            arrayList.add(new Pair(FILMNAME_KEY, FILMNAME_VALUE))
            arrayList.add(new Pair(FILMYEAR_KEY, FILMYEAR_VALUE))
            when(upflixParser.getAllUpflixesFromWeb(anyString(), anyString()))
                    .thenReturn(Arrays.asList(createUpflix(DIST_CHOICE, ID).build(),createUpflix(DIST_CHOICE, ID).build()))
        when:
            def eventFlux = createGetRequest("/upflix", arrayList)
        then:
            def parse = jsonslurpe.parse(eventFlux.returnResult().responseBody)

            assert parse.get(0).id == ID
            assert parse.get(0).distributionChoice == DIST_CHOICE
            assert parse.size() == 2
            def first = movieRepository.findAll().blockFirst()
            assert first.title == FILMNAME_VALUE
            assert first.year == FILMYEAR_VALUE
//            1 * movieService.save(_)
//            1 * upflixParser.getAllUpflixesFromWeb(*_)
    }

}

