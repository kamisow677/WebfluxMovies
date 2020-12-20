package com.kamil.merchants

import com.kamil.merchants.infrastructure.repository.Movie
import com.kamil.merchants.kafka.KafkaService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Unroll
import utils.BaseIntegration

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.when
import static utils.UpflixTestBilder.createUpflixDistrChoice
import static utils.UpflixTestBilder.baseUpflix
import static utils.MovieTestBilder.createMovie

@SpringBootTest
class UplixServiceSpec extends BaseIntegration {

    @MockBean
    com.kamil.merchants.infrastructure.parser.UpflixParserImpl upflixParser

    @MockBean(name = "KafkaService")
    KafkaService kafkaService

    public static final String FILMNAME_KEY = "filmName"
    public static final String FILMNAME_VALUE = "filmName_value"
    public static final String FILMYEAR_KEY = "filmYear"
    public static final String FILMYEAR_VALUE = "filmYear_value"
    public static final String DIST_CHOICE = "distChoice"
    public static final String ID = "1"

    @Shared
    Movie dummy = createMovie().upflixes([baseUpflix().build()]).build()

    def "when context is loaded then all expected beans are created"() {
        expect: "the context is created"
            upflixServiceResponseHandler
    }

    def cleanupper() {
        movieRepository.deleteAll().block()
    }

    def "get all upflixes"() {
        given:
            cleanupper()
            movieRepository.save(dummy).block()
        when:
            def eventFlux = createGetAllRequest("/upflixAll")
        then:
            def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)
            assert parse.get(0).link == dummy.upflixes[0].link
            assert parse.get(0).siteName == dummy.upflixes[0].siteName

    }

    def "get upflixes by id"() {
        given:
            cleanupper()
            Movie dummy = createMovie("3") .upflixes([createUpflixDistrChoice("TestowyChoice").build()]).build()
            movieRepository.save(dummy).block()
        when:
            def eventFlux = createMovieGetRequest("/movie/3")
        then:
            def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)
            assert parse.upflixes.get(0).link == dummy.upflixes.get(0).link
            assert parse.upflixes.get(0).distributionChoice == dummy.upflixes.get(0).distributionChoice

    }

    def "delete upflixes"() {
        given:
            cleanupper()
            movieRepository.save(dummy).block()
        when:
            def eventFlux = createDeleteRequest("/movie/1")
        then:
            noExceptionThrown()
            assert null == eventFlux.returnResult().responseBody
    }

    @Unroll
    def "save upflix #dummyExistsInDB"() {
        given:
            cleanupper()
            if (dummyExistsInDB == true)
                movieRepository.save(expected).block()
        when:
            def eventFlux = movieRepository.save(dummy)
        then:
            StepVerifier.create(eventFlux)
                    .expectNext(expected)
                    .verifyComplete()
            StepVerifier.create(movieRepository.findById(dummy.getId()))
                    .expectNextCount(1)
                    .verifyComplete()
        where:
            dummyExistsInDB || expected
            true            || dummy
            false           || dummy
    }

    def "getUpflixMovieData"() {
        given:
            cleanupper()
            ArrayList arrayList = new ArrayList()
            arrayList.add(new Tuple(FILMNAME_KEY, FILMNAME_VALUE))
            arrayList.add(new Tuple(FILMYEAR_KEY, FILMYEAR_VALUE))
            when(upflixParser.getAllUpflixesFromWeb(anyString(), anyString()))
                    .thenReturn(Arrays.asList(createUpflixDistrChoice(DIST_CHOICE, ID).build(),createUpflixDistrChoice(DIST_CHOICE, ID).build()))
            doNothing().when(kafkaService).sendMovieToKafka(any())
        when:
            def eventFlux = createGetUpflixRequest("/upflix", arrayList)
        then:
            def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)

            assert parse.upflixes.get(0).id == ID
            assert parse.upflixes.get(0).distributionChoice == DIST_CHOICE
            assert parse.upflixes.size() == 2
            def first = movieRepository.getAllMovies().blockFirst()
            assert first.title == FILMNAME_VALUE
            assert first.year == FILMYEAR_VALUE
    }

}

