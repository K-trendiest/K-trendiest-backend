package com.ktrendiest.newton.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Movie {
    private final String rank;
    private final String title;
    private final String imageLink;
    private final String infoLink;
}
