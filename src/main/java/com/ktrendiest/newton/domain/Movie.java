package com.ktrendiest.newton.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Movie {
    private final String rank;
    private final String name;
    private final String imageLink;
    private final String infoLink;

    public Movie(String rank, String name, String imageLink, String infoLink) {
        this.rank = rank;
        this.name = name;
        this.imageLink = imageLink;
        this.infoLink = infoLink;
    }
}
