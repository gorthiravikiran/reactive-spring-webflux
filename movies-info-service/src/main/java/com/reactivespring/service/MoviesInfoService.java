package com.reactivespring.service;

import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.exception.MoviesInfoException;
import com.reactivespring.repository.MoviesInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MoviesInfoService {

    MoviesInfoRepository moviesInfoRepository;

    public MoviesInfoService(MoviesInfoRepository moviesInfoRepository) {
        this.moviesInfoRepository = moviesInfoRepository;
    }

    public Mono<MoviesInfoDTO> addMovieInfo(MoviesInfoDTO moviesInfoDTO) throws MoviesInfoException {
        String movieInfoId = moviesInfoDTO.getMovieInfoIdForVerification();
        return moviesInfoRepository.existsById(movieInfoId)
                .map(Optional::of)
                .flatMap(exists -> {
                    if (exists.isPresent() && Boolean.TRUE.equals(exists.get()))
                        throw new MoviesInfoException("MovieInfo Already Exists in Db");

                    return Mono.just(moviesInfoDTO)
                            .map(MoviesInfoDTO::toEntity)
                            .flatMap(movieInfo -> moviesInfoRepository.save(movieInfo))
                            .map(MoviesInfoDTO::new);
                });
    }

    public Flux<MoviesInfoDTO> getAllMovies() {
        return moviesInfoRepository.findAll().map(MoviesInfoDTO::new);
    }

    public Mono<MoviesInfoDTO> getMovieById(String movieId) {
        return moviesInfoRepository.findById(movieId).map(MoviesInfoDTO::new);
    }

    public Mono<MoviesInfoDTO> updateMovieById(String movieId, MoviesInfoDTO moviesInfoDTO) {
        return moviesInfoRepository.findById(movieId)
                .flatMap(existingMovie -> {
                    existingMovie.setName(moviesInfoDTO.getName());
                    existingMovie.setYear(moviesInfoDTO.getYear());
                    existingMovie.setCast(moviesInfoDTO.getCast());
                    existingMovie.setReleaseDate(LocalDate.parse(moviesInfoDTO.getReleaseDate()));
                    return moviesInfoRepository.save(existingMovie);
                }).map(MoviesInfoDTO::new);
    }

    public Mono<Void> deleteMovieById(String movieId) {
        return moviesInfoRepository.deleteById(movieId);
    }

    public Mono<Void> deleteAllMovies() {
        return moviesInfoRepository.deleteAll();
    }

    public Flux<MoviesInfoDTO> getMovieInfosByYear(Integer year) {
        return moviesInfoRepository.findByYear(year).map(MoviesInfoDTO::new);
    }
}
