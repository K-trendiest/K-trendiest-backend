package com.ktrendiest.newton.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ktrendiest.newton.constant.UrlConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktrendiest.newton.domain.Youtube;

import static com.ktrendiest.newton.constant.DisplayConstant.TOTAL_ITEMS_NUMBER;

@Service
public class YoutubeService {
    private final String youtubeKey;

    YoutubeService(@Value("${youtube-key}") String youtubeKey) {
        this.youtubeKey = youtubeKey;
    }

    public List<Youtube> getYoutubeInfos() {
        String apiUrl = getApiData();
        String jsonData = getYoutubeResponseBody(apiUrl);

        return getYoutubesFromJson(jsonData);
    }

    private String getApiData() {
        return UriComponentsBuilder.fromHttpUrl(UrlConstant.YOUTUBE_API_URL)
                .queryParam("part", "snippet")
                .queryParam("chart", "mostPopular")
                .queryParam("maxResults", TOTAL_ITEMS_NUMBER)
                .queryParam("regionCode", "kr")
                .queryParam("key", youtubeKey)
                .toUriString();
    }

    private String getYoutubeResponseBody(String apiUrl) {
        return WebClient.create()
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private List<Youtube> getYoutubesFromJson(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = null;

        try {
            root = objectMapper.readTree(jsonData)
                    .path("items");
            createYoutubes(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        return createYoutubes(root);
    }

    private List<Youtube> createYoutubes(JsonNode root) {
        List<Youtube> youtubes = new ArrayList<>();
        int i = 1;

        for (JsonNode video : root) {
            String rank = Integer.toString(i++);
            String title = extractTitle(video);
            String imageLink = extractImageLink(video);
            String infoLink = extractInfoLink(video);

            youtubes.add(Youtube.builder()
                    .rank(rank)
                    .title(title)
                    .imageLink(imageLink)
                    .infoLink(infoLink)
                    .build());
        }
        return youtubes;
    }

    private String extractTitle(JsonNode video) {
        return video.path("snippet")
                .path("title")
                .asText();
    }

    private String extractImageLink(JsonNode video) {
        return video.path("snippet")
                .path("thumbnails")
                .path("standard")
                .path("url")
                .asText();
    }

    private String extractInfoLink(JsonNode video) {
        return UrlConstant.VIDEO_BASE_URL + video.path("id")
                .asText();
    }
}
