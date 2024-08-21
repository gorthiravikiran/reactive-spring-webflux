package com.reactivespring.integration;

import com.reactivespring.domain.MoviesInfo;
import com.reactivespring.repository.MoviesInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

    @Autowired
    MoviesInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        MoviesInfo movieInfo = new MoviesInfo("123", "Batman Begins", 2005, List.of("Chritian Bale", "Michael Cane"), LocalDate.of(2005, 6, 15));
        MoviesInfo movieInfo1 = new MoviesInfo("124", "The Dark Knight", 2008, List.of("Chritian Bale", "HeathLedger"), LocalDate.of(2008, 7, 18));
        MoviesInfo movieInfo2 = new MoviesInfo("125", "The Dark Knight Rises", 2012, List.of("Chritian Bale", "Tom Hardy"), LocalDate.of(2012, 7, 20));
        var movieInfos = List.of(movieInfo, movieInfo1, movieInfo2);
        movieInfoRepository.saveAll(movieInfos).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {
        // Given

        // When
        var movieInfoFlux = movieInfoRepository.findAll().log();

        // Then
        StepVerifier // (1)
                .create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        // Given

        // When
        var movieInfoMono = movieInfoRepository.findById("124").log();

        // Then
        StepVerifier // (1)
                .create(movieInfoMono)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assert movieInfo != null;
                    assert movieInfo.getMovieInfoId().equals("124");
                    assert movieInfo.getName().equals("The Dark Knight");
                    assert movieInfo.getYear().equals(2008);
                    assert movieInfo.getCast().contains("Chritian Bale");
                    assert movieInfo.getCast().contains("HeathLedger");
                    assert movieInfo.getReleaseDate().equals(LocalDate.of(2008, 7, 18));
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        // Given
        MoviesInfo movieInfo = new MoviesInfo(null, "Inception", 2010, List.of("Leonardo DiCaprio", "Joseph Gordon-Levitt"), LocalDate.of(2010, 7, 16));

        // When
        var savedMovieInfoMono = movieInfoRepository.save(movieInfo).log();

        // Then
        StepVerifier // (1)
                .create(savedMovieInfoMono)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1 != null;
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assert movieInfo1.getName().equals("Inception");
                    assert movieInfo1.getYear().equals(2010);
                    assert movieInfo1.getCast().contains("Leonardo DiCaprio");
                    assert movieInfo1.getCast().contains("Joseph Gordon-Levitt");
                    assert movieInfo1.getReleaseDate().equals(LocalDate.of(2010, 7, 16));
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        // Given
        var movieInfo = movieInfoRepository.findById("124").block();
        movieInfo.setYear(2021);

        // When
        var savedMovieInfoMono = movieInfoRepository.save(movieInfo).log();

        // Then
        StepVerifier // (1)
                .create(savedMovieInfoMono)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1 != null;
                    assert movieInfo1.getMovieInfoId().equals("124");
                    assert movieInfo1.getYear().equals(2021);
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        // Given

        // When
        movieInfoRepository.deleteById("124").block();
        var movieInfoFlux = movieInfoRepository.findAll().log();

        // Then
        StepVerifier // (1)
                .create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

}