package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DictionaryClueService {
    private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getClue(String word) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + word))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                return parseDefinition(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generateFallbackClue(word);
    }

    private String parseDefinition(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Safely navigate JSON with null checks
            JsonNode firstMeaning = rootNode.path(0)
                    .path("meanings")
                    .path(0);

            if (firstMeaning.isMissingNode()) {
                return generateFallbackClue("");
            }

            JsonNode firstDefinition = firstMeaning.path("definitions")
                    .path(0)
                    .path("definition");

            return firstDefinition.isMissingNode()
                    ? generateFallbackClue("")
                    : firstDefinition.asText();

        } catch (Exception e) {
            return generateFallbackClue("");
        }
    }

    private String generateFallbackClue(String word) {
        String[] fallbacks = {
                "Definition of " + word,
                "Relating to " + word,
                word.toUpperCase() + " means...",
                "What is " + word + "?"
        };
        return fallbacks[(int)(Math.random() * fallbacks.length)];
    }
}