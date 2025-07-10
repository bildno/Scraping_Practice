package com.example.scraping.Scraping_Practice.controller;

import com.example.scraping.Scraping_Practice.dto.NewsDto;
import com.example.scraping.Scraping_Practice.dto.PushRequest;
import com.example.scraping.Scraping_Practice.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/news")
public class NewsController {



    private final NewsService newsService;


    public NewsController(NewsService service){
        this.newsService = service;
    }


    @GetMapping("/stablecoin/{keyword}")
    public List<NewsDto> getStablecoinNew(@PathVariable String keyword) throws IOException {
        return newsService.fetchStablecoinNews(keyword);
    }


    @PostMapping("/send-push")
    public ResponseEntity<String> sendPush(@RequestBody PushRequest request) {
        return newsService.sendPushNotification(request);
    }


    @PostMapping("/send-push-delayed")
    public ResponseEntity<String> sendPushDelayed(@RequestBody PushRequest request) {
        return newsService.sendPushDelayed(request);
    }

}
