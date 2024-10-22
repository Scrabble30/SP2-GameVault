package app.services.impl;

import app.dtos.PlatformDTO;
import app.services.PlatformService;
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

public class PlatformServiceImpl implements PlatformService {

    private final Logger logger = LoggerFactory.getLogger(PlatformService.class);

    private final String BASE_URL = "https://api.rawg.io/api";
    private final String API_KEY = System.getenv("API_KEY");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public PlatformServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Set<PlatformDTO> getPlatforms() {
        String uri = String.format("%s/platforms/lists/parents?key=%s", BASE_URL, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "Application/json")
                .uri(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(Set.class, PlatformDTO.class);
                JsonNode jsonNode = objectMapper.readTree(response.body());

                return objectMapper.treeToValue(jsonNode.get("results"), type);
            } else {
                logger.error("GET request {} failed. Status code: {}", uri, response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to retrieve platforms: {} {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }
}
