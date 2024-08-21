package com.reactivespring.repository;

import com.reactivespring.domain.MoviesInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MoviesInfoRepository extends ReactiveMongoRepository<MoviesInfo, String> {
}
