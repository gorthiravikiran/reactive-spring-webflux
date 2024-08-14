package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = MonoAndFluxController.class)
@AutoConfigureWebTestClient
class MonoAndFluxControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void returnFlux() {
        webTestClient.get().uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void returnFlux_Approach2() {
        var flux = webTestClient.get().uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(1,2,3, 4)
                .verifyComplete();
    }

    @Test
    void returnFlux_Approach3() {
        webTestClient.get().uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                        .consumeWith(response -> assertEquals(4, Objects.requireNonNull(response.getResponseBody()).size()));
    }

    @Test
    void returnMono() {
        webTestClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello World");
    }

    @Test
    void returnMono_Approach3() {
        webTestClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> assertEquals("Hello World", response.getResponseBody()));
    }

    @Test
    void returnStream() {
        webTestClient.get().uri("/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Long.class)
                .hasSize(5);
    }

    @Test
    void returnStream_Approach2() {
        var flux = webTestClient.get().uri("/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(flux)
                .expectNext(0L, 1L ,2L)
                .thenCancel()
                .verify();
    }

    @Test
    void returnFluxStream() {
        webTestClient.get().uri("/fluxStream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .hasSize(4);
    }
}