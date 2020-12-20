package utils

import com.kamil.merchants.infrastructure.repository.Movie
import com.kamil.merchants.infrastructure.repository.Upflix

class MovieTestBilder {

    private static Movie.MovieBuilder baseMovie()
    {
        return Movie
                .builder()
                .id("1")
                .title("title")
                .year("year")
    }

    static Movie.MovieBuilder createMovie() {
        return baseMovie()
    }

    static Movie.MovieBuilder createMovie(String id) {
        return baseMovie().id(id)
    }

}

class UpflixTestBilder {

    static Upflix.UpflixBuilder baseUpflix()
    {
        return Upflix
                .builder()
                .id("1")
                .link("Link")
                .distributionChoice("Abonament")
                .siteName("site")

    }

    static Upflix.UpflixBuilder createUpflixDistrChoice(String distrChoice) {
        return baseUpflix()
                .distributionChoice(distrChoice)

    }

    static Upflix.UpflixBuilder createUpflixDistrChoice(String distrChoice, String id) {
        return baseUpflix()
                .distributionChoice(distrChoice)
                .id(id)

    }

}
