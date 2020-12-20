package com.kamil.merchants.kafka;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(MoviesStream.class)
public class StreamsConfig {
}