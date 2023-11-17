package com.ktrendiest.newton.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import com.ktrendiest.newton.domain.Music;

@Service
public class MusicService {
    public List<Music> getMusicInfos(){
        String url = "https://www.melon.com/chart/index.htm";
        Document document = getDocument(url);

        List<String> titles = extractTextFromHtml(document, ".wrap_song_info .rank01 span a");
        List<String> artistNames = extractTextFromHtml(document, ".wrap_song_info .rank02 a");
        List<String> imageLinks = extractAttrFromHtml(document, ".image_typeAll img", "src");
        List<String> resizeImageLinks = resizeImageLinks(imageLinks);
        List<String> songIds = extractAttrFromHtml(document, ".d_song_list tbody tr","data-song-no");
        List<String> infoLinks = getInfoLinksOfSongIds(songIds);

        return createMusicList(titles, artistNames, resizeImageLinks, infoLinks);
    }

    private Document getDocument(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return document;
    }

    private List<String> extractTextFromHtml(Document doc, String selector) {
        return doc.select(selector).stream()
                .map(Element::text)
                .collect(Collectors.toList());
    }

    private List<String> extractAttrFromHtml(Document doc, String selector, String attr) {
        return doc.select(selector).stream()
                .map(element -> element.attr(attr))
                .collect(Collectors.toList());
    }

    public List<String> resizeImageLinks(List<String> imageLinks) {
        return imageLinks.stream()
                .map(this::replaceResizeAndQuality)
                .collect(Collectors.toList());
    }

    private String replaceResizeAndQuality(String originalLink) {
        return originalLink.replaceAll("resize/120/quality/80", "resize/300/quality/100");
    }

    private List<String> getInfoLinksOfSongIds(List<String> songIds) {
        List<String> infoLinks = new ArrayList<>();
        String baseUrl = "https://www.melon.com/song/detail.htm?songId=";

        for (int i = 0; i < 10; i++) {
            infoLinks.add(baseUrl + songIds.get(i));
        }
        return infoLinks;
    }

    private List<Music> createMusicList(List<String> titles, List<String> artistNames, List<String> imageLinks, List<String> infoLinks) {
        List<Music> musics = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            musics.add(Music.builder()
                    .rank(Integer.toString(i + 1))
                    .title(titles.get(i))
                    .artistName(artistNames.get(i))
                    .imageLink(imageLinks.get(i))
                    .infoLink(infoLinks.get(i))
                    .build());
        }
        return musics;
    }
}
