package com.reactivespring.integration;

import com.reactivespring.domain.MoviesInfo;
import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.exception.MoviesInfoException;
import com.reactivespring.repository.MoviesInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

    private static final String MOVIE_ADD_URL = "/moviesInfo/add";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MoviesInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        MoviesInfo movieInfo = new MoviesInfo("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), LocalDate.of(2005, 6, 15));
        MoviesInfo movieInfo1 = new MoviesInfo("The Dark Knight-2008", "The Dark Knight", 2008, List.of("Chritian Bale", "HeathLedger"), LocalDate.of(2008, 7, 18));
        MoviesInfo movieInfo2 = new MoviesInfo("The Dark Knight Rises-2012", "The Dark Knight Rises", 2012, List.of("Chritian Bale", "Tom Hardy"), LocalDate.of(2012, 7, 20));
        var movieInfos = List.of(movieInfo, movieInfo1, movieInfo2);
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        //given
        var moviesInfo = new MoviesInfo(null, "Inception", 2010, List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"), LocalDate.of(2010, 7, 16));

        //when
        webTestClient.post()
                .uri(MOVIE_ADD_URL)
                .bodyValue(moviesInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MoviesInfoDTO.class)
                .consumeWith(moviesInfoEntityExchangeResult -> {
                    var savedMovieInfo = moviesInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assertEquals("Inception-2010", savedMovieInfo.getMovieInfoId());
                });

        //then
    }

    @Test
    void addMovieInfo_Failure() {
        //given
        var moviesInfo = new MoviesInfo("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), LocalDate.of(2005, 6, 15));

        //when
        webTestClient.post()
                .uri(MOVIE_ADD_URL)
                .bodyValue(moviesInfo)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(MoviesInfoException.class);
    }

    @Test
    void getMovieInfo() {
        webTestClient.get()
                .uri("/moviesInfo/get/The Dark Knight-2008")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MoviesInfoDTO.class)
                .consumeWith(moviesInfoEntityExchangeResult -> {
                    var movieInfo = moviesInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                    assertEquals("The Dark Knight", movieInfo.getName());
                });
    }

    @Test
    void getAllMovies() {
        webTestClient.get()
                .uri("/moviesInfo/getAllMoviesInfo")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MoviesInfoDTO.class)
                .hasSize(3);
    }

    @Test
    void updateMovieInfo() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("The Dark Knight-2008", "The Dark Knight", 2008, List.of("Chritian Bale", "HeathLedger"), "2008-07-21");

        //when
        webTestClient.put()
            .uri("/moviesInfo/update/The Dark Knight-2008")
            .bodyValue(moviesInfoDTO)
            .exchange()
            .expectStatus().isAccepted()
            .expectBody(MoviesInfoDTO.class)
            .consumeWith(moviesInfoEntityExchangeResult1 -> {
                var updatedMovieInfo = moviesInfoEntityExchangeResult1.getResponseBody();
                assert updatedMovieInfo != null;
                assertEquals("The Dark Knight", updatedMovieInfo.getName());
                assertEquals("The Dark Knight-2008", updatedMovieInfo.getMovieInfoId());
                assertEquals(2008, updatedMovieInfo.getYear());
                assertEquals("2008-07-21", updatedMovieInfo.getReleaseDate());
            });

        //then
    }

    @Test
    void deleteMovieInfo() {
        webTestClient.delete()
                .uri("/moviesInfo/delete/The Dark Knight-2008")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Void.class);
    }

    @Test
    void deleteAllMovieInfo() {
        webTestClient.delete()
                .uri("/moviesInfo/deleteAll")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Void.class);
    }
}