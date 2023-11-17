package com.ktrendiest.newton.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktrendiest.newton.domain.Movie;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MovieService {
    @Value("${kofic-key}")
    private String koficKey;
    @Value("${kmdb-key}")
    private String kmdbKey;
    private final List<String> titles;
    private final List<String> ranks;
    private final List<String> openDates;
    private final List<String> imageLinks;
    private final List<String> kmdbUrls;
    private final WebClient webClient;

    public MovieService() {
        titles = new ArrayList<>();
        ranks = new ArrayList<>();
        openDates = new ArrayList<>();
        imageLinks = new ArrayList<>();
        kmdbUrls = new ArrayList<>();
        webClient = WebClient.create();
    }

    public List<Movie> getMovieInfos() {
        String jsonData1 = getDataFromKoficApi();
        addRankAndName(jsonData1);
        fetchDataFromKmdbApi();
        return createMovies();
    }

    private List<Movie> createMovies() {
        List<Movie> movies = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            movies.add(new Movie(ranks.get(i), titles.get(i), imageLinks.get(i), kmdbUrls.get(i)));
        }

        return movies;
    }

    private String getDataFromKoficApi() {
        String targetDate = getTargetDate();
        String baseUrl = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
        String dynamicUrl = getKoficDynamicUrl(baseUrl, targetDate);

        return getKoficResponseBody(dynamicUrl);
    }

    private void fetchDataFromKmdbApi() {
        String baseUrl = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2";

        for (int i = 0; i < 10; i++) {
            String dynamicUrl = getKmdbDynamicUrl(baseUrl, i);
            String responseBody = getKmdbResponseBody(dynamicUrl);

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                addKmdbUrlAndPosters(rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addRankAndName(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            for (JsonNode dailyBoxOffice : jsonNode.path("boxOfficeResult").path("dailyBoxOfficeList")) {
                ranks.add(dailyBoxOffice.path("rank").asText());
                titles.add(dailyBoxOffice.path("movieNm").asText());
                LocalDate openDt = LocalDate.parse(dailyBoxOffice
                        .path("openDt")
                        .asText());
                openDates.add(outputFormatter.format(openDt));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTargetDate() {
        LocalDate currentDateTime = LocalDate.now();
        LocalDate yesterdayDateTime = currentDateTime.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return formatter.format(yesterdayDateTime);
    }

    private String getKoficDynamicUrl(String baseUrl, String targetDate) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("key", koficKey)
                .queryParam("targetDt", targetDate)
                .build()
                .toUriString();
    }

    private String getKoficResponseBody(String dynamicUrl) {
        return webClient.get()
                .uri(dynamicUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String getKmdbDynamicUrl(String baseUrl, int num) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("ServiceKey", kmdbKey)
                .queryParam("title", titles.get(num))
                .queryParam("releaseDts", openDates.get(num))
                .build()
                .toUriString();
    }

    private String getKmdbResponseBody(String dynamicUrl) {
        return webClient.get()
                .uri(dynamicUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    private void addKmdbUrlAndPosters(JsonNode rootNode) {
        JsonNode resultNode = rootNode
                .path("Data")
                .path(0)
                .path("Result");

        JsonNode postersNode = resultNode
                .path(0)
                .path("posters");
        JsonNode kmdbUrlNode = resultNode
                .path(0)
                .path("kmdbUrl");

        imageLinks.add(postersNode.asText().split("\\|")[0]);
        kmdbUrls.add(kmdbUrlNode.asText());
    }
}