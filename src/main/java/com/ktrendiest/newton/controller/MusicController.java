package com.ktrendiest.newton.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.ktrendiest.newton.domain.Music;
import com.ktrendiest.newton.service.MusicService;

@RestController
public class MusicController {
    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/music")
    public List<Music> getMusic() {
        return musicService.getMusicInfos();
    }
}
