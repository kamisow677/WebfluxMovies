package com.kamil.merchants.infrastructure.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Flux<Object> getAllDistinctUpflixes() {
        return reactiveMongoTemplate.query(Movie.class).distinct("upflixes").all();
    }

    public Flux<Movie> getAllMoviesOnSiteName(String siteName) {


//        GroupOperation groupByStateAndSumPop = group("state")
//                .sum("pop").as("statePop");
//        MatchOperation filterStates = match(new Criteria("statePop").gt(10000000));
//        SortOperation sortByPopDesc = sort(Sort.by(Direction.DESC, "statePop"));
//
//        Aggregation aggregation = newAggregation(
//                groupByStateAndSumPop, filterStates, sortByPopDesc);
//        AggregationResults<StatePopulation> result = mongoTemplate.aggregate(
//                aggregation, "zips", StatePopulation.class);
//


        MatchOperation filterStates = match(Criteria.where("year").is("1995"));
        SortOperation sort = sort(Sort.Direction.DESC, "upflixes.siteName");
        GroupOperation groupByStateAndSumPop = group("id","title","year", "upflixes");

        Aggregation agg = Aggregation.newAggregation(
                filterStates,
                groupByStateAndSumPop
//                project()
//                        .andExpression("title").as("title")
//                        .andExpression("year").as("year")
//                        .andExpression("id").as("id")
//                        .andExpression("upflixes").as("upflixes")
        );

        return reactiveMongoTemplate.aggregate(agg, "zips", Movie.class);
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
}

@CrossOrigin(origins = "*")
interface GeneratedMovieRepository extends ReactiveCrudRepository<Movie, String> {
    Mono<Movie> findByTitle(String title);
}
