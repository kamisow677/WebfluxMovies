package com.kamil.merchants.infrastructure;

import com.kamil.merchants.Generator;
import com.kamil.merchants.infrastructure.parser.UpflixParser;
import com.kamil.merchants.infrastructure.repository.*;
import com.kamil.merchants.kafka.KafkaService;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceResponseHandler {


    @Autowired
    UpflixParser upflixParser;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    Generator generator;


    public Mono<ServerResponse> getUpflixMovieData(ServerRequest request) {

        String filmName = request.queryParam("filmName").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "FileName is bad"));
        String filmYear = request.queryParam("filmYear").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "filmYear is bad"));

        List<Upflix> upflixes = upflixParser.getAllUpflixesFromWeb(filmName, filmYear);
        upflixes = upflixes.stream()
                .map(upflix -> movieRepository.addLinkToUpflix(upflix))
                .collect(Collectors.toList());

        Mono<Movie> movieMono = save(generator.generateUUID(), filmName, filmYear, upflixes);

//        kafkaService.sendMovieToKafka(movieMono);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(movieMono, Upflix.class);
    }

    public Mono<ServerResponse> getAllMovies(ServerRequest request) {
        Flux<Movie> upflixFlux = movieRepository.getAllMovies();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }

    public Mono<ServerResponse> updateUpflixlink(ServerRequest request) {
        String siteName = request.queryParam("siteName").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "FileName is bad"));;
        String link = request.queryParam("link").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "FileName is bad"));;
        Mono<UpdateResult> updateResultMono = movieRepository.updateLinkToUpflix(siteName, link);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(updateResultMono, Upflix.class);
    }

    public Mono<ServerResponse> getAllUpflixes(ServerRequest request) {
        Flux<Upflix> upflixFlux = movieRepository.getAllDistinctUpflixes();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }

    public Mono<ServerResponse> getAllMoviesOnSiteName(ServerRequest request) {
        String siteName = request.pathVariable("siteName");
        Flux<Movie> upflixFlux = movieRepository.getAllMoviesOnSiteName(siteName);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(upflixFlux, Upflix.class);
    }

    public Mono<ServerResponse> getBest(ServerRequest request) {
        Flux<UpflixCount> upflixFlux = movieRepository.getBestUpflixCount().sort(Comparator.comparing(UpflixCount::getId));
        Flux<Upflix> allDistinctUpflixes = movieRepository.getAllDistinctUpflixes().sort(Comparator.comparing(Upflix::getSiteName));

        Flux<UpflixWithCount> zip = Flux.zip(
                upflixFlux,
                allDistinctUpflixes,
                (upflixCount, upflix) -> {
                    UpflixWithCount upflixWithCount = new UpflixWithCount(upflix);
                    upflixWithCount.setCount(upflixCount.getCount());
                    return upflixWithCount;
                }
        ).sort((obj1, obj2) -> (obj1.getCount() > obj2.getCount()) ? -1 : 1);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(zip, Upflix.class);
    }

    public Mono<ServerResponse> deleteMovieById(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Void> voidMono = movieRepository.deleteById(movieId);
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> deleteAllMovies(ServerRequest request) {
        Mono<Void> voidMono = movieRepository.deleteAll();
        return ServerResponse.ok().build(voidMono);
    }

    public Mono<ServerResponse> getMovieById(ServerRequest request) {
        String movieId = request.pathVariable("id");
        Mono<Movie> upflixMono = movieRepository.getById(movieId)
                .switchIfEmpty(Mono.error(new Exception("No Movie  was found with id:  " + movieId)));
        return upflixMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> saveMovie(ServerRequest request) {
        Mono<Movie> movieMono = request.bodyToMono(Movie.class)
                .flatMap(movie -> save(generator.generateUUID(), movie.getTitle(), movie.getYear(), null));

        return movieMono.flatMap(data -> ServerResponse.ok().bodyValue(data))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getImage(ServerRequest request) {
        String title = request.queryParam("title").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is bad"));
        var byTitle = movieRepository.findByTitle(title)
                .map(it-> it.getImage());

        return ServerResponse.ok().body(byTitle, byte[].class);
    }

    public Mono<ServerResponse> uploadImage(ServerRequest request) {
        String title = request.queryParam("title").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Site Name is bad"));


        var then = request.multipartData().map(it -> it.get("files"))
                .flatMapMany(Flux::fromIterable)
                .cast(FilePart.class)
                .flatMap(it -> {
                        it.transferTo(Paths.get(it.filename()));
                        return Mono.just(it);
                    }
                )
                .flatMap(it -> {
                    File file = new File(it.filename());
                    byte[] fileContent = new byte[0];
                    try {
                        fileContent = Files.readAllBytes(file.toPath());


                        BufferedImage img = ImageIO.read(file);


                        BufferedImage bufferedImage = resizeImage(img, 400, 400);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "jpg", baos);
                        fileContent = baos.toByteArray();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return movieRepository.addImageToMovie(title, fileContent);

                });


        return ServerResponse.ok().body(then, UpdateResult.class);
    }

    BufferedImage resizeImage(BufferedImage originalImage, double targetWidth, double targetHeight) throws IOException {
        double height = originalImage.getHeight();
        double width = originalImage.getWidth();
            double i =  width / targetWidth;
            targetHeight = height / i;
        BufferedImage resizedImage = new BufferedImage((int) targetWidth, (int) targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.drawImage(originalImage, 0, 0, (int) targetWidth, (int) targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public Mono<Movie> save(String id, String title, String year, List<Upflix> upflixList) {
        return movieRepository.findByTitle(title)
                .switchIfEmpty(movieRepository.save(
                        Movie.builder()
                                .id(id)
                                .title(title)
                                .year(year)
                                .upflixes(upflixList)
                                .build()
                        )
                );
    }


    public Mono<ServerResponse> admin(ServerRequest serverRequest) {
        Mono<String> admin = Mono.just("Admin");
        return ServerResponse.ok().body(admin, String.class);
    }
}
