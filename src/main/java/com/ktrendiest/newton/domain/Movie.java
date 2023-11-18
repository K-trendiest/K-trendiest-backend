package com.ktrendiest.newton.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Movie {
    private final String rank;
    private final String title;
    private final String imageLink;
    private final String infoLink;
}
