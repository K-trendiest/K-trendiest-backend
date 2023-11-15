package com.ktrendiest.newton.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Test
    public void testGetMovieInfo() {
        // 테스트할 로직을 작성
        movieService.getMovieInfo();
    }
}