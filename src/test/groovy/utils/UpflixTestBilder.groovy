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

     Movie toMovie()
    {
        return Movie
                .builder()
                .id(id ? id : "id")
                .title(title ? title : "title")
                .year(year ? year : "year")
                .upflixes(upflixes ? upflixes : [])
                .build()
    }

}

@Builder(prefix = "with")
class UpflixTestBilder {

     String siteName
     String link
     String distributionChoice

    Upflix toUpflix() {
        return Upflix
                .builder()
                .link( link ? link :"link")
                .distributionChoice(distributionChoice ? distributionChoice : "distributionChoice")
                .siteName(siteName ? siteName : "siteName")
                .build()
    }

}
