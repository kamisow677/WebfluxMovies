package utils

import com.kamil.merchants.infrastructure.repository.Movie
import com.kamil.merchants.infrastructure.repository.Upflix
import groovy.transform.builder.Builder

@Builder(prefix = "with")
class MovieTestBilder {

     String id
     String title
     String year
     List<Upflix> upflixes

    MovieTestBilder() {
        this.id = "id"
        this.title = "title"
        this.year = "year"
        this.upflixes = new ArrayList<>()
    }

     Movie toMovie()
    {
        return Movie
                .builder()
                .id(this.id)
                .title(this.title)
                .year(this.year)
                .upflixes(this.upflixes)
                .build()
    }

}

@Builder(prefix = "with")
class UpflixTestBilder {

     String siteName = "site"
     String link = "Link"
     String distributionChoice = "Abonament"

    UpflixTestBilder() {
        this.siteName = "siteName"
        this.link = "link"
        this.distributionChoice = "distributionChoice"
    }

    Upflix toUpflix()
    {
        return Upflix
                .builder()
                .link(this.link)
                .distributionChoice(this.distributionChoice)
                .siteName(this.siteName)
                .build()

    }

}
