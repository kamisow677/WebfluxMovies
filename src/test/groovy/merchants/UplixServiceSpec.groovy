package com.kamil.merchants

import com.kamil.merchants.infrastructure.repository.Movie
import com.kamil.merchants.kafka.KafkaService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Unroll
import utils.BaseIntegration
import utils.MovieTestBilder
import utils.UpflixTestBilder

import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.doNothing
import static org.mockito.Mockito.when

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
    public static final String ID = "3"

    MovieTestBilder movieTestBilder = new MovieTestBilder()
    UpflixTestBilder upflixTestBilder = new UpflixTestBilder()

    @Shared
    Movie dummy

    def setup(){
        movieTestBilder.setUpflixes([upflixTestBilder.toUpflix()])
        dummy = movieTestBilder.toMovie()
    }

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
            def upflixTestBilder = UpflixTestBilder.builder().withDistributionChoice("TestowyChoice").build()
            dummy = MovieTestBilder.builder()
                    .withId(ID)
                    .withUpflixes([upflixTestBilder.toUpflix()])
                    .build()
                    .toMovie()

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
        when:
            movieRepository.save(dummy).block()
        then:
            StepVerifier.create(movieRepository.findById(dummy.getId()))
                    .expectNextCount(1)
                    .verifyComplete()
    }

    def "should save in database movie with all upflixes"() {
        given:
            cleanupper()
            ArrayList arrayList = new ArrayList()
            arrayList.add(new Tuple(FILMNAME_KEY, FILMNAME_VALUE))
            arrayList.add(new Tuple(FILMYEAR_KEY, FILMYEAR_VALUE))

            def upflixTestBilder = new UpflixTestBilder()
            upflixTestBilder.setDistributionChoice(DIST_CHOICE)

            when(upflixParser.getAllUpflixesFromWeb(anyString(), anyString()))
                    .thenReturn(
                            Arrays.asList(
                                    upflixTestBilder.toUpflix(),
                                    upflixTestBilder.toUpflix()
                            )
                    )
            doNothing().when(kafkaService).sendMovieToKafka(any())
        when:
            def eventFlux = createGetUpflixRequest("/upflix", arrayList)
        then:
            def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)

            assert parse.upflixes.get(0).distributionChoice == DIST_CHOICE
            assert parse.upflixes.size() == 2
            def first = movieRepository.getAllMovies().blockFirst()
            assert first.title == FILMNAME_VALUE
            assert first.year == FILMYEAR_VALUE
    }

    def "should get all upflixes by movies count"() {
        given:
            cleanupper()
            def u1 = UpflixTestBilder.builder().withSiteName("siteName1").build().toUpflix()
            def u2 = UpflixTestBilder.builder().withSiteName("siteName2").build().toUpflix()
            def u3 = UpflixTestBilder.builder().withSiteName("siteName3").build().toUpflix()

            movieRepository.save(
                    MovieTestBilder.builder()
                        .withId("1")
                        .withUpflixes([u1,u2])
                        .build().toMovie()
            ).block()
            movieRepository.save(
                    MovieTestBilder.builder()
                        .withId("2")
                        .withUpflixes([u1,u3])
                        .build().toMovie()
            ).block()
        when:
            def eventFlux = createGetMovieExtraRequest("/movie/best/best")
        then:
            def parse = jsonslurper.parse(eventFlux.returnResult().responseBody)
            parse.max {it -> it.count}.with {
                assert count == 2
                assert siteName == "siteName1"
                assert link == "link"
                assert distributionChoice == "distributionChoice"
            }
    }

}

