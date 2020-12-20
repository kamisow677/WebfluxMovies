package com.kamil.merchants.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

@Component
public interface MoviesStream {

    String OUTPUT = "movies-out";

    @Output(OUTPUT)
    MessageChannel outboundGreetings();
}