package com.reactivespring.controller;

import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.exception.MoviesInfoException;
import com.reactivespring.service.MoviesInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/moviesInfo")
public class MoviesInfoController {

    MoviesInfoService moviesInfoService;

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<MoviesInfoDTO>> addMovieInfo(@RequestBody @Valid MoviesInfoDTO moviesInfoDTO) throws MoviesInfoException {
        return moviesInfoService.addMovieInfo(moviesInfoDTO)
                .map(moviesInfoDTO1 -> ResponseEntity.ok(moviesInfoDTO1))
                .onErrorResume(MoviesInfoException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/get/{movieId}")
    public Mono<ResponseEntity<MoviesInfoDTO>> getMovieInfo(@PathVariable String movieId) {
        return moviesInfoService.getMovieById(movieId)
                .map(moviesInfoDTO -> ResponseEntity.ok(moviesInfoDTO))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/get")
    public Flux<MoviesInfoDTO> getMovieInfosByYear(@RequestParam(value = "year", required = true) Integer year) {
        return moviesInfoService.getMovieInfosByYear(year).log();
    }

    @GetMapping("/getAllMoviesInfo")
    public Flux<MoviesInfoDTO> getAllMovies() {
        return moviesInfoService.getAllMovies();
    }

    @PutMapping("/update/{movieId}")
    public Mono<ResponseEntity<MoviesInfoDTO>> updateMovieInfo(@PathVariable String movieId, @RequestBody MoviesInfoDTO moviesInfoDTO) {
        return moviesInfoService.updateMovieById(movieId, moviesInfoDTO)
                .map(moviesInfoDTO1 -> ResponseEntity.ok(moviesInfoDTO1))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/delete/{movieId}")
    public Mono<Void> deleteMovieInfo(@PathVariable String movieId) {
        return moviesInfoService.deleteMovieById(movieId);
    }

    @DeleteMapping("/deleteAll")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteAllMovies() {
        return moviesInfoService.deleteAllMovies();
    }

}
