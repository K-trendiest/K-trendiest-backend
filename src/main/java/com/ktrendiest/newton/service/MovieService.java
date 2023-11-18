package com.ktrendiest.newton.service;

import static com.ktrendiest.newton.constant.DisplayConstant.TOTAL_ITEMS_NUMBER;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktrendiest.newton.domain.Movie;
import com.ktrendiest.newton.constant.UrlConstant;

@Service
public class MovieService {
    private final String koficKey;
    private final String kmdbKey;
    private final List<String> titles = new ArrayList<>();
    private final List<String> ranks = new ArrayList<>();
    private final List<String> openDates = new ArrayList<>();
    private final List<String> imageLinks = new ArrayList<>();
    private final List<String> infoLinks = new ArrayList<>();
    private List<Movie> movieInfos;

    public MovieService(@Value("${kofic-key}") String koficKey,
                        @Value("${kmdb-key}") String kmdbKey) {
        this.koficKey = koficKey;
        this.kmdbKey = kmdbKey;
    }

    public List<Movie> getMovieInfos() {
        return movieInfos;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 1 * * *")
    private void initialize() {
        fetchDataFromKofic();
        fetchDataFromKmdb();
        createMovies();
    }

    private void fetchDataFromKofic() {
        String targetDate = getTargetDate();
        String baseUrl = UrlConstant.KOFIC_BASE_URL;
        String koficUrl = getKoficDynamicUrl(baseUrl, targetDate);
        String koficData = getKoficResponseBody(koficUrl);
        addRankAndName(koficData);
    }

    private void fetchDataFromKmdb() {
        String baseUrl = UrlConstant.KMDB_BASE_URL;

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
        return  WebClient.create()
                .get()
                .uri(dynamicUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
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

    private String getKmdbDynamicUrl(String baseUrl, int num) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("ServiceKey", kmdbKey)
                .queryParam("title", titles.get(num))
                .queryParam("releaseDts", openDates.get(num))
                .build()
                .toUriString();
    }

    private String getKmdbResponseBody(String dynamicUrl) {
        return WebClient.create()
                .get()
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
        infoLinks.add(kmdbUrlNode.asText());
    }

    private void createMovies() {
        List<Movie> movies = new ArrayList<>();

        for (int i = 0; i < TOTAL_ITEMS_NUMBER; i++) {
            movies.add(Movie.builder()
                    .rank(ranks.get(i))
                    .title(titles.get(i))
                    .imageLink(imageLinks.get(i))
                    .infoLink(infoLinks.get(i))
                    .build());
        }
        movieInfos = movies;
    }
}