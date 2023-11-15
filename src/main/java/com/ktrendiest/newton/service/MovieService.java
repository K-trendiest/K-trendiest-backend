package com.ktrendiest.newton.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktrendiest.newton.domain.Movie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {
    @Value("${kobis-key}")
    private String kobisKey;
    private final List<String> names;
    private final List<String> ranks;

    public MovieService() {
        names = new ArrayList<>();
        ranks = new ArrayList<>();
    }

    public Movie getMovieInfo() {
        String jsonData1 = fetchDataFromKoficApi();
        addRankAndName(jsonData1);

        for (String name : names) {
            System.out.println(name);
        }

        return null;
    }

    public String fetchDataFromKoficApi() {
        LocalDate currentDateTime = LocalDate.now();
        LocalDate yesterdayDateTime = currentDateTime.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedYesterdayDate = yesterdayDateTime.format(formatter);

        String apiUrl = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=" + kobisKey + "&targetDt=" + formattedYesterdayDate;
        System.out.println(apiUrl);
        WebClient webClient = WebClient.create();

        return webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void addRankAndName(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            for (JsonNode dailyBoxOffice : jsonNode.path("boxOfficeResult").path("dailyBoxOfficeList")) {
                ranks.add(dailyBoxOffice.path("rank").asText());
                names.add(dailyBoxOffice.path("movieNm").asText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
