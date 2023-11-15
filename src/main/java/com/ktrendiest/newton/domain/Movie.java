package com.ktrendiest.newton.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Movie {
    String rank;
    String name;
    String imageUrl;
    String connectUrl;

    public Movie(String rank, String name) {
        this.rank = rank;
        this.name = name;
    }
}
