package utils

import com.kamil.merchants.movie.MovieRepository
import com.kamil.merchants.movie.MovieService
import com.kamil.merchants.upflix.UpflixParser
import com.kamil.merchants.upflix.UpflixRepository
import com.kamil.merchants.upflix.UpflixRouter
import com.kamil.merchants.upflix.UpflixService
import groovy.json.JsonSlurper
import javafx.util.Pair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunction
import spock.lang.Specification

class BaseIntegration extends Specification {

    @Autowired
    UpflixService upflixService

    @Autowired
    UpflixRepository upflixRepository

    @Autowired
    MovieRepository movieRepository

    @Autowired
    MovieService movieService

    WebTestClient clientUpflixes
    WebTestClient clientUpflix
    JsonSlurper jsonslurpe = new JsonSlurper()

    def setup() {
        RouterFunction<?> routeFlux = (new UpflixRouter()).routeUpflixAll(upflixService)
        clientUpflixes = WebTestClient.bindToRouterFunction(routeFlux).build()

        RouterFunction<?> routeMono = (new UpflixRouter()).routeUpflix(upflixService)
        clientUpflix = WebTestClient.bindToRouterFunction(routeMono).build()
    }

    def cleanup() {
        upflixRepository.deleteAll().block()
    }

    WebTestClient.BodyContentSpec createGetAllRequest(String url) {
        return createbaseRequest(url, clientUpflixes.get(), new ArrayList<String>())
    }

    WebTestClient.BodyContentSpec createGetRequest(String url, List<String> params = new ArrayList()) {
        return createbaseRequest(url, clientUpflix.get(), params)
    }

    WebTestClient.BodyContentSpec createDaleteRequest(String url) {
        return createbaseRequest(url, clientUpflix.delete(), new ArrayList<String>())
    }

    WebTestClient.BodyContentSpec createSaveRequest(String url) {
        return createbaseRequest(url, clientUpflix.post(), new ArrayList<String>())
    }

    private createbaseRequest(String url, WebTestClient.RequestHeadersUriSpec<?> st , List<Pair<String,String>> params) {
        if (params.size() != 0) {
            def u = { uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam(params.get(0).getKey(), params.get(0).getValue())
                        .queryParam(params.get(1).getKey(), params.get(1).getValue())
                        .build()
            }
            return st.uri(u)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
        }
        return st.uri(url)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
    }

}