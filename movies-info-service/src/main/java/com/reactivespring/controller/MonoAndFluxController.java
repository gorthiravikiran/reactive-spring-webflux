package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class MonoAndFluxController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4).log();
    }

    @GetMapping("/mono")
    public Mono<String> returnMono() {
        return Mono.just("Hello World").log();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> returnStream() {
        return Flux.interval(Duration.ofSeconds(1)).take(5).log();
    }

    @GetMapping(value = "/fluxStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> returnFluxStream() {
        return Flux.just(1, 2, 3, 4).delayElements(Duration.ofSeconds(1)).log();
    }
}
