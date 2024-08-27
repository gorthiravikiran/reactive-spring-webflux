package com.reactivespring.repository;

import com.reactivespring.domain.MoviesInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MoviesInfoRepository extends ReactiveMongoRepository<MoviesInfo, String> {

    Flux<MoviesInfo> findByYear(Integer year);
}
