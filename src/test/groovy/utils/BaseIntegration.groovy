package utils

import com.kamil.merchants.infrastructure.ServiceResponseHandler
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunction
import spock.lang.Specification

@ActiveProfiles("test")
class BaseIntegration extends Specification {

    @Autowired
    ServiceResponseHandler upflixServiceResponseHandler

    @Autowired
    com.kamil.merchants.infrastructure.repository.MovieRepository movieRepository

    WebTestClient clientUpflix
    WebTestClient clientUpflixes
    WebTestClient clientMovie
    JsonSlurper jsonslurper = new JsonSlurper()

    def setup() {
        RouterFunction<?> routeFlux = (new com.kamil.merchants.infrastructure.api.UpflixRouter()).routeUpflix(upflixServiceResponseHandler)
        clientUpflix = WebTestClient.bindToRouterFunction(routeFlux).build()

        routeFlux = (new com.kamil.merchants.infrastructure.api.UpflixRouter()).routeUpflixes(upflixServiceResponseHandler)
        clientUpflixes = WebTestClient.bindToRouterFunction(routeFlux).build()

        RouterFunction<?> routeMono = (new com.kamil.merchants.infrastructure.api.MovieRouter()).routeMovie(upflixServiceResponseHandler)
        clientMovie = WebTestClient.bindToRouterFunction(routeMono).build()
    }

    def cleanup() {
        movieRepository.deleteAll().block()
    }

    WebTestClient.BodyContentSpec createGetAllRequest(String url) {
        return createbaseRequest(url, clientUpflixes.get(), new ArrayList<String>())
    }

    WebTestClient.BodyContentSpec createMovieGetRequest(String url, List<String> params = new ArrayList()) {
        return createbaseRequest(url, clientMovie.get(), params)
    }

    WebTestClient.BodyContentSpec createGetUpflixRequest(String url, List<String> params = new ArrayList()) {
        return createbaseRequest(url, clientUpflix.get(), params)
    }

    WebTestClient.BodyContentSpec createDeleteRequest(String url) {
        return createbaseRequest(url, clientMovie.delete(), new ArrayList<String>())
    }

    private createbaseRequest(String url, WebTestClient.RequestHeadersUriSpec<?> st , List<Tuple> params) {
        if (params.size() != 0) {
            def u = { uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam(params.get(0)[0], params.get(0)[1])
                        .queryParam(params.get(1)[0], params.get(1)[1])
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