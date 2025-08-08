package com.example.sample_spring_boot.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;
import com.google.common.hash.Hashing;

@Service
public class ShortenURLService {

    @Autowired
    private UrlRepository urlRepository;

    public String generateShortCode(String originalUrl, Optional<String> customCode, Optional<Integer> userId) {
        if (userId.isPresent()){ // signed in user can create as many custom short URLs as they want
            String shortCode = customCode.isPresent() ? customCode.get() : shortenUrl(originalUrl + userId.get() + System.currentTimeMillis());
            Url newUrl = new Url(originalUrl, shortCode, userId.get());
            urlRepository.save(newUrl);
            return shortCode;
        } else {
            // don't care customCode for anonymous user
            Optional<Url> byOriginalUrl = urlRepository.findByOriginalUrl(originalUrl);
            if (byOriginalUrl.isPresent()) {
                return byOriginalUrl.get().getShortCode();
            }
            String shortCode = shortenUrl(originalUrl);
            urlRepository.save(new Url(originalUrl, shortCode, -1));
            return shortCode;
        }
    }

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    // Convert byte array to Base62 string
    private static String encodeBase62(byte[] bytes) {
        BigInteger number = new BigInteger(1, bytes); // Unsigned
        StringBuilder result = new StringBuilder();

        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(BASE));
            result.append(BASE62_ALPHABET.charAt(divmod[1].intValue()));
            number = divmod[0];
        }

        return result.reverse().toString();
    }

    // Generate short code from long URL
    public static String shortenUrl(String longUrl) {
        byte[] hashBytes = Hashing.md5()
                .hashString(longUrl, StandardCharsets.UTF_8)
                .asBytes();

        String base62Encoded = encodeBase62(hashBytes);
        return base62Encoded.substring(0, 8); // Adjust length as needed
    }
}
