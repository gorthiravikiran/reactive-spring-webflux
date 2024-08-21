package com.reactivespring.controller;

import com.reactivespring.domain.MoviesInfo;
import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.exception.MoviesInfoException;
import com.reactivespring.repository.MoviesInfoRepository;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {

    private static final String MOVIE_ADD_URL = "/moviesInfo/add";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MoviesInfoController moviesInfoController;

    @MockBean
    MoviesInfoService moviesInfoServiceMock;

    @MockBean
    MoviesInfoRepository moviesInfoRepositoryMock;

    MoviesInfo movieInfo, movieInfo1, movieInfo2;

    @BeforeEach
    void setUp() {
        movieInfo = new MoviesInfo("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), LocalDate.of(2005, 6, 15));
        movieInfo1 = new MoviesInfo("The Dark Knight-2008", "The Dark Knight", 2008, List.of("Chritian Bale", "HeathLedger"), LocalDate.of(2008, 7, 18));
        movieInfo2 = new MoviesInfo("The Dark Knight Rises-2012", "The Dark Knight Rises", 2012, List.of("Chritian Bale", "Tom Hardy"), LocalDate.of(2012, 7, 20));
        var movieInfos = List.of(movieInfo, movieInfo1, movieInfo2);
        when(moviesInfoServiceMock.getAllMovies()).thenReturn(Flux.fromIterable(movieInfos).map(MoviesInfoDTO::new));
    }

    @Test
    void addMovieInfo() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("Inception-2010", "Inception", 2010, List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"), "2010-07-16");

        //when
        when(moviesInfoServiceMock.getMovieById("Inception-2010")).thenReturn(Mono.empty());
        when(moviesInfoServiceMock.addMovieInfo(moviesInfoDTO)).thenReturn(Mono.just(moviesInfoDTO));

        //then
        webTestClient.post()
                .uri(MOVIE_ADD_URL)
                .bodyValue(moviesInfoDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MoviesInfoDTO.class)
                .consumeWith(moviesInfoEntityExchangeResult -> {
                    var savedMovieInfo = moviesInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assertEquals("Inception-2010", savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfo_validation() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("Inception-2010", null, -2010, List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"), "2010-07-16");

        //then
        webTestClient.post()
                .uri(MOVIE_ADD_URL)
                .bodyValue(moviesInfoDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(moviesInfoEntityExchangeResult -> {
                    var errorMessage = moviesInfoEntityExchangeResult.getResponseBody();
                    System.out.println("errorMessage = " + errorMessage);
                    assert errorMessage != null;
//                    assertEquals("MoviesInfo.name cannot be null/empty", errorMessage);
                });
    }

    @Test
    void addMovieInfo_Failure() {
        //given
        MoviesInfoDTO moviesInfo = new MoviesInfoDTO("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), "2005-06-15");

        //when
        doThrow(MoviesInfoException.class).when(moviesInfoServiceMock).addMovieInfo(moviesInfo);

        //then
        webTestClient.post()
                .uri(MOVIE_ADD_URL)
                .bodyValue(moviesInfo)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getMovieInfo() {
        //when
        when(moviesInfoServiceMock.getMovieById("The Dark Knight-2008")).thenReturn(Mono.just(movieInfo1).map(MoviesInfoDTO::new));

        //then
        webTestClient.get()
                .uri("/moviesInfo/get/The Dark Knight-2008")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MoviesInfoDTO.class)
                .consumeWith(moviesInfoEntityExchangeResult -> {
                    var movieInfoResult = moviesInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoResult != null;
                    assertEquals("The Dark Knight", movieInfoResult.getName());
                });
    }

    @Test
    void getAllMovies() {
        //when
        when(moviesInfoServiceMock.getAllMovies()).thenReturn(Flux.just(movieInfo, movieInfo1, movieInfo2).map(MoviesInfoDTO::new));

        //then
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
        MoviesInfo moviesInfo = moviesInfoDTO.toEntity();

        //when
        when(moviesInfoServiceMock.getMovieById("The Dark Knight-2008")).thenReturn(Mono.just(moviesInfo).map(MoviesInfoDTO::new));
        when(moviesInfoServiceMock.updateMovieById("The Dark Knight-2008", moviesInfoDTO)).thenReturn(Mono.just(moviesInfo).map(MoviesInfoDTO::new));

        //then
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
    }

    @Test
    void deleteMovieInfo() {
        //when
        when(moviesInfoServiceMock.deleteMovieById("The Dark Knight-2008")).thenReturn(Mono.empty());

        //then
        webTestClient.delete()
                .uri("/moviesInfo/delete/The Dark Knight-2008")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Void.class);
    }

    @Test
    void deleteAllMovieInfo() {
        //when
        when(moviesInfoServiceMock.deleteAllMovies()).thenReturn(Mono.empty());

        //then
        webTestClient.delete()
                .uri("/moviesInfo/deleteAll")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(Void.class);
    }
}