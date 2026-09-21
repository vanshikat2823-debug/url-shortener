package com.vanshika.urlshortener.controller;

import com.vanshika.urlshortener.dto.CreateUrlRequest;
import com.vanshika.urlshortener.dto.CreateUrlResponse;
import com.vanshika.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin(origins = "*")
public class urlController {

    private final UrlService urlService;

    public urlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    public CreateUrlResponse createUrl(@RequestBody CreateUrlRequest request) {

        String shortCode = urlService.createShortUrl(
                request.getOriginalUrl()
        );

        String shortUrl = "http://localhost:8080/" + shortCode;

        return new CreateUrlResponse(shortUrl);
    }
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        String originalUrl = urlService.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}