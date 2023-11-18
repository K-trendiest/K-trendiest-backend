package com.ktrendiest.newton.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ktrendiest.newton.domain.Youtube;
import com.ktrendiest.newton.service.YoutubeService;

@RestController
public class YoutubeController {
    private final YoutubeService youtubeService;

    @Autowired
    public YoutubeController(YoutubeService youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/youtubes")
    public List<Youtube> getYoutubes() {
        return youtubeService.getYoutubeInfos();
    }
}
