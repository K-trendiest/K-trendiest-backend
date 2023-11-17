package com.ktrendiest.newton.domain;

import lombok.Builder;

@Builder
public class Music {
    private final String rank;
    private final String title;
    private final String artistName;
    private final String imageLink;
    private final String infoLink;
}
