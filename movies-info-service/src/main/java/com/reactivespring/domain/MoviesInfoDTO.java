package com.reactivespring.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviesInfoDTO {
    @Id
    private String movieInfoId;
    @NotBlank(message="MoviesInfo.name cannot be null/empty")
    private String name;
    @NotNull(message="MoviesInfo.year cannot be null/empty")
    @Positive
    private Integer year;
    private List<@NotBlank(message="MoviesInfo.cast cannot be null/empty") String> cast;
    @NotNull(message="MoviesInfo.releaseDate cannot be null/empty")
    private String releaseDate;

    public MoviesInfoDTO(MoviesInfo moviesInfo) {
        this.movieInfoId = moviesInfo.getMovieInfoId();
        this.name = moviesInfo.getName();
        this.year = moviesInfo.getYear();
        this.cast = moviesInfo.getCast();
        this.releaseDate = moviesInfo.getReleaseDate().toString();
    }

    public MoviesInfo toEntity() {
        this.setMovieInfoId(this.getName()+"-"+this.getYear());
        return new MoviesInfo(this.getMovieInfoId(), this.getName(), this.getYear(), this.getCast(), LocalDate.parse(this.getReleaseDate()));
    }

    public String getMovieInfoIdForVerification() {
        return this.getName()+"-"+this.getYear();
    }

}
