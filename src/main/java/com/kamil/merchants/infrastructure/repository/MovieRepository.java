package com.kamil.merchants.infrastructure.repository;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@Slf4j
public class MovieRepository {

    @Autowired
    GeneratedMovieRepository generatedMovieRepository;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Movie> findByTitle(String title) {
        return generatedMovieRepository.findByTitle(title);
    }

    public Mono<Movie> findById(String id) {
        return generatedMovieRepository.findById(id);
    }

    public Mono<Movie> save(Movie movie) {
        return generatedMovieRepository.save(movie);
    }

    public Mono<UpdateResult> updateLinkToUpflix(String sitename, String link) {
        Query query = new Query(
                Criteria.where("upflixes.siteName").is(sitename)
        );
        Update update = new Update().set("upflixes.$.link", link);
        return reactiveMongoTemplate.updateMulti(query,update, Movie.class);
    }


    public Flux<Upflix> getAllDistinctUpflixes() {
        return reactiveMongoTemplate.query(Movie.class).distinct("upflixes").all()
                .map(it-> {
                    if (it instanceof Upflix)
                        return (Upflix)it;
                    else
                        throw new MappingException("Object is not Upflix");
                })
                .distinct(a -> a.getSiteName());
    }

    public Flux<Movie> getAllMoviesOnSiteName(String siteName) {
        MatchOperation filterStates = match(Criteria.where("upflixes.siteName").is(siteName));
        Aggregation agg = Aggregation.newAggregation(
                filterStates,
                project()
                        .and(ArrayOperators.Filter.filter("upflixes")
                                .as("item")
                                .by(ComparisonOperators.Eq.valueOf("item.siteName").equalToValue(siteName))
                        )
                        .as("upflixes")
                        .andExpression("title").as("title")
                        .andExpression("year").as("year")
        );

        return reactiveMongoTemplate.aggregate(agg, "movie", Movie.class);
    }

    public Flux<UpflixCount> getBestUpflixCount() {

        Aggregation agg = Aggregation.newAggregation(
                unwind("upflixes"),
                Aggregation.group("upflixes.siteName").count().as("count")
        );

        return reactiveMongoTemplate.aggregate(agg, "movie", UpflixCount.class);
    }

    public Mono<Void> deleteById(String movieId) {
        return generatedMovieRepository.deleteById(movieId);
    }

    public Mono<Void> deleteAll() {
        return generatedMovieRepository.deleteAll();
    }

    public Mono<Movie> getById(String movieId) {
        return generatedMovieRepository.findById(movieId);
    }

    public Flux<Movie> getAllMovies() {
        return generatedMovieRepository.findAll();
    }

    public Upflix addLinkToUpflix(Upflix upflix) {
        Query query = new Query(
                Criteria.where("upflixes.siteName").is(upflix.getSiteName())
        );
        reactiveMongoTemplate.find(query, Movie.class)
                .map(movie -> {
                            List<Upflix> collect = movie.getUpflixes().stream()
                                    .filter(u -> u.getSiteName().equals(upflix.getSiteName()))
                                    .collect(Collectors.toList());
                            if (collect.size() != 0)
                                upflix.setLink(collect.get(0).getLink());
                            return movie;
                        }
                ).blockFirst();
        return upflix;

    }

    public Mono<UpdateResult> addImageToMovie(String title, byte[] filePart) {
        Query query = new Query(
                Criteria.where("title").is(title)
        );
        Update update = new Update().set("image", filePart);
        return reactiveMongoTemplate.updateMulti(query,update, Movie.class);
    }
}

@CrossOrigin(origins = "*")
interface GeneratedMovieRepository extends ReactiveCrudRepository<Movie, String> {
    Mono<Movie> findByTitle(String title);
}
