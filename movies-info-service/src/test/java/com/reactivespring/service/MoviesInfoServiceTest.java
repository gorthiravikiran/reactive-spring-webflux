package com.reactivespring.service;

import com.reactivespring.domain.MoviesInfo;
import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.repository.MoviesInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoService.class)
@AutoConfigureWebTestClient
class MoviesInfoServiceTest {

    @Autowired
    MoviesInfoService moviesInfoService;

    @MockBean
    MoviesInfoRepository moviesInfoRepositoryMock;

    MoviesInfo movieInfo, movieInfo1, movieInfo2;

    @BeforeEach
    void setUp() {
        movieInfo = new MoviesInfo("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), LocalDate.of(2005, 6, 15));
        movieInfo1 = new MoviesInfo("The Dark Knight-2008", "The Dark Knight", 2008, List.of("Chritian Bale", "HeathLedger"), LocalDate.of(2008, 7, 18));
        movieInfo2 = new MoviesInfo("The Dark Knight Rises-2012", "The Dark Knight Rises", 2012, List.of("Chritian Bale", "Tom Hardy"), LocalDate.of(2012, 7, 20));
        var movieInfos = List.of(movieInfo, movieInfo1, movieInfo2);
        when(moviesInfoRepositoryMock.findAll()).thenReturn(Flux.fromIterable(movieInfos));
    }

    @Test
    void addMovieInfo() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("Inception-2010", "Inception", 2010, List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"), "2010-07-16");
        MoviesInfo savedMoviesInfo = moviesInfoDTO.toEntity();

        //when
        when(moviesInfoRepositoryMock.findById("Inception-2010")).thenReturn(Mono.empty());
        when(moviesInfoRepositoryMock.existsById("Inception-2010")).thenReturn(Mono.just(Boolean.FALSE));
        when(moviesInfoRepositoryMock.save(savedMoviesInfo)).thenReturn(Mono.just(savedMoviesInfo));

        //then
        moviesInfoService.addMovieInfo(moviesInfoDTO).subscribe(moviesInfo -> {
            assertEquals(moviesInfoDTO.getMovieInfoIdForVerification(), moviesInfo.getMovieInfoIdForVerification());
            assertEquals(moviesInfoDTO.getName(), moviesInfo.getName());
            assertEquals(moviesInfoDTO.getYear(), moviesInfo.getYear());
            assertEquals(moviesInfoDTO.getCast(), moviesInfo.getCast());
            assertEquals(moviesInfoDTO.getReleaseDate(), moviesInfo.getReleaseDate());
        });
    }

    @Test
    void getAllMovies() {
        moviesInfoService.getAllMovies().collectList().subscribe(moviesInfoDTOS -> {
            assertEquals(3, moviesInfoDTOS.size());
            assertEquals("Batman Begins", moviesInfoDTOS.get(0).getName());
            assertEquals("The Dark Knight", moviesInfoDTOS.get(1).getName());
            assertEquals("The Dark Knight Rises", moviesInfoDTOS.get(2).getName());
        });
    }

    @Test
    void getMovieById() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), "2005-06-15");
        MoviesInfo updatedMoviesInfo = moviesInfoDTO.toEntity();

        //when
        when(moviesInfoRepositoryMock.findById("Batman Begins-2005")).thenReturn(Mono.just(updatedMoviesInfo));

        //then
        moviesInfoService.getMovieById("Batman Begins-2005").subscribe(m ->
            assertEquals("Batman Begins", m.getName())
        );
    }

    @Test
    void updateMovieById() {
        //given
        MoviesInfoDTO moviesInfoDTO = new MoviesInfoDTO("Batman Begins-2005", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), "2005-06-15");
        MoviesInfo updatedMoviesInfo = moviesInfoDTO.toEntity();

        //when
        when(moviesInfoRepositoryMock.findById("Batman Begins-2005")).thenReturn(Mono.just(movieInfo));
        when(moviesInfoRepositoryMock.save(updatedMoviesInfo)).thenReturn(Mono.just(updatedMoviesInfo));

        //then
        moviesInfoService.updateMovieById("Batman Begins-2005", moviesInfoDTO).subscribe(moviesInfo -> {
            assertEquals(moviesInfoDTO.getMovieInfoIdForVerification(), moviesInfo.getMovieInfoIdForVerification());
            assertEquals(moviesInfoDTO.getName(), moviesInfo.getName());
            assertEquals(moviesInfoDTO.getYear(), moviesInfo.getYear());
            assertEquals(moviesInfoDTO.getCast(), moviesInfo.getCast());
            assertEquals(moviesInfoDTO.getReleaseDate(), moviesInfo.getReleaseDate());
        });
    }

    @Test
    void deleteMovieById() {
        //when
        when(moviesInfoRepositoryMock.deleteById("Batman Begins-2005")).thenReturn(Mono.empty());

        //then
        moviesInfoService.deleteMovieById("Batman Begins-2005").subscribe(Assertions::assertNull);
    }

    @Test
    void deleteAllMovies() {
        //when
        when(moviesInfoRepositoryMock.deleteAll()).thenReturn(Mono.empty());

        //then
        moviesInfoService.deleteAllMovies().subscribe(Assertions::assertNull);
    }
}