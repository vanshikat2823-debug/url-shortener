package com.vanshika.urlshortener.service;

import com.vanshika.urlshortener.entity.Url;
import com.vanshika.urlshortener.exception.UrlNotFoundException;
import com.vanshika.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }
    public String createShortUrl(String originalUrl) {
        if (!isValidUrl(originalUrl)) {
            throw new IllegalArgumentException("Invalid URL");
        }
        String shortCode;

        do {
            shortCode = UUID.randomUUID()
                    .toString()
                    .substring(0, 6);

        } while (urlRepository.existsByShortCode(shortCode));
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);
        url.setCreatedAt(java.time.LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusHours(24));
        urlRepository.save(url);

        return shortCode;
    }
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));
        // Check if URL has expired
        if (url.getExpiresAt() != null &&
                !url.getExpiresAt().isAfter(LocalDateTime.now())){

            throw new UrlNotFoundException("URL has expired");
        }

        // Increase click count
        url.setClickCount(url.getClickCount() + 1);

        // Save updated click count
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    private boolean isValidUrl(String originalUrl) {

        if (originalUrl == null || originalUrl.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(originalUrl);

            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;

        } catch (URISyntaxException e) {
            return false;
        }
    }
}
