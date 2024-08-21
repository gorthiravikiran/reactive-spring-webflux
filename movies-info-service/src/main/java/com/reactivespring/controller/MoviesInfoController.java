package com.reactivespring.controller;

import com.reactivespring.domain.MoviesInfo;
import com.reactivespring.domain.MoviesInfoDTO;
import com.reactivespring.exception.MoviesInfoException;
import com.reactivespring.service.MoviesInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/moviesInfo")
public class MoviesInfoController {

    MoviesInfoService moviesInfoService;

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MoviesInfoDTO> addMovieInfo(@RequestBody @Valid MoviesInfoDTO moviesInfoDTO) throws MoviesInfoException {
        return moviesInfoService.addMovieInfo(moviesInfoDTO);
    }

    @GetMapping("/get/{movieId}")
    public Mono<MoviesInfoDTO> getMovieInfo(@PathVariable String movieId) {
        return moviesInfoService.getMovieById(movieId);
    }

    @GetMapping("/getAllMoviesInfo")
    public Flux<MoviesInfoDTO> getAllMovies() {
        return moviesInfoService.getAllMovies();
    }

    @PutMapping("/update/{movieId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<MoviesInfoDTO> updateMovieInfo(@PathVariable String movieId, @RequestBody MoviesInfoDTO moviesInfoDTO) {
        return moviesInfoService.updateMovieById(movieId, moviesInfoDTO);
    }

    @DeleteMapping("/delete/{movieId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteMovieInfo(@PathVariable String movieId) {
        return moviesInfoService.deleteMovieById(movieId);
    }

    @DeleteMapping("/deleteAll")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> deleteAllMovieInfo() {
        return moviesInfoService.deleteAllMovies();
    }

}
