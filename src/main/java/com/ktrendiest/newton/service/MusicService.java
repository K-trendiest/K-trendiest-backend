package com.ktrendiest.newton.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ktrendiest.newton.constant.DisplayConstant;
import com.ktrendiest.newton.constant.UrlConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import com.ktrendiest.newton.domain.Music;

import static com.ktrendiest.newton.constant.DisplayConstant.QUALITY_SETTING_NUMBER;
import static com.ktrendiest.newton.constant.DisplayConstant.RESIZE_SETTING_NUMBER;
import static com.ktrendiest.newton.constant.DisplayConstant.TOTAL_ITEMS_NUMBER;

@Service
public class MusicService {
    public List<Music> getMusicInfos(){
        String url = UrlConstant.MUSIC_BASE_URL;
        Document document = getDocument(url);

        List<String> titles = extractTextFromHtml(document, ".wrap_song_info .rank01 span a");
        List<String> artistNames = extractTextFromHtml(document, ".service_list_song .wrap_song_info .rank02 span a");
        List<String> imageLinks = extractAttrFromHtml(document, ".image_typeAll img", "src");
        List<String> resizeImageLinks = resizeImageLinks(imageLinks);
        List<String> songIds = extractAttrFromHtml(document, ".d_song_list tbody tr","data-song-no");
        List<String> infoLinks = getInfoLinksOfSongIds(songIds);

        for (String artistName : artistNames) {
            System.out.println(artistName);
        }

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
                .toList();
    }

    private List<String> extractAttrFromHtml(Document doc, String selector, String attr) {
        return doc.select(selector).stream()
                .map(element -> element.attr(attr))
                .toList();
    }

    public List<String> resizeImageLinks(List<String> imageLinks) {
        return imageLinks.stream()
                .map(this::replaceResizeAndQuality)
                .toList();
    }

    private String replaceResizeAndQuality(String originalLink) {
        return originalLink.replaceAll("resize/120/quality/80", "resize" + RESIZE_SETTING_NUMBER + "quality" + QUALITY_SETTING_NUMBER);
    }

    private List<String> getInfoLinksOfSongIds(List<String> songIds) {
        List<String> infoLinks = new ArrayList<>();
        String baseUrl = UrlConstant.SONG_ID_URL;

        for (int i = 0; i < TOTAL_ITEMS_NUMBER; i++) {
            infoLinks.add(baseUrl + songIds.get(i));
        }
        return infoLinks;
    }

    private List<Music> createMusicList(List<String> titles, List<String> artistNames, List<String> imageLinks, List<String> infoLinks) {
        List<Music> musics = new ArrayList<>();

        for (int i = 0; i < TOTAL_ITEMS_NUMBER; i++) {
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
