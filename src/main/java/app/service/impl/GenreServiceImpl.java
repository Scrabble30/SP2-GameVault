package app.service.impl;

import app.dto.GenreDTO;
import app.service.GenreService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class GenreServiceImpl implements GenreService {

    private final Logger logger = LoggerFactory.getLogger(GenreService.class);

    private final String BASE_URL = "https://api.rawg.io/api";
    private final String API_KEY = System.getenv("API_KEY");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public GenreServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Set<GenreDTO> getGenres() {
        String uri = String.format("%s/genres?key=%s", BASE_URL, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "Application/json")
                .uri(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(Set.class, GenreDTO.class);
                JsonNode jsonNode = objectMapper.readTree(response.body());

                return objectMapper.treeToValue(jsonNode.get("results"), type);
            } else {
                logger.error("GET request {} failed. Status code: {}", uri, response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to retrieve genres: {} {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }
}
