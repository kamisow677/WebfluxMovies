package com.kamil.merchants.kafka;

import com.kamil.merchants.infrastructure.repository.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@Slf4j
@EnableBinding(MoviesStream.class)
@EnableAutoConfiguration
public class KafkaService {

    private static final String[] PartitionKeys = new String[]{
            "partitionKey1", "partitionKey2", "partitionKey3",
    };

    private final MoviesStream moviesStream;
    private final Random radom = new Random();
    private final boolean AUTOGENERATE_MESSAGES = false;

    public KafkaService(MoviesStream moviesStream) {
        this.moviesStream = moviesStream;
    }

    public void sendMovieToKafka(Mono<Movie> movie) {
        log.info("Sending movie {}", movie.toString());
        MessageChannel messageChannel = moviesStream.outboundGreetings();
        messageChannel.send(MessageBuilder
                .withPayload(movie)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    @InboundChannelAdapter(channel = MoviesStream.OUTPUT, poller = @Poller(fixedRate = "10000"))
    public Message<?> generate() {
        if (AUTOGENERATE_MESSAGES==true) {
            String key = PartitionKeys[radom.nextInt(PartitionKeys.length)];
            System.out.println(key);
            Movie movie = Movie.builder().id("1").title("TEST").build();
            System.out.println("Sending: " + movie);
            return MessageBuilder.withPayload(movie)
                    .setHeader("partitionKey", key)
                    .build();
        }
        else return null;
    }

}
