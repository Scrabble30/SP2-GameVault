package app.service.impl;

import app.dto.GameDTO;
import app.service.GameService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public class GameServiceImpl implements GameService {

    private final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    private final String BASE_URL = "https://api.rawg.io/api/games";
    private final String API_KEY = System.getenv("API_KEY");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public GameServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Set<GameDTO> getAllGames() {
        int amountOfGames = 25;
        String uri = String.format("%s?key=%s&page_size=%s", BASE_URL, API_KEY, amountOfGames);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "Application/json")
                .uri(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JavaType classType = objectMapper.getTypeFactory().constructCollectionType(Set.class, GameDTO.class);
                JsonNode jsonNode = objectMapper.readTree(response.body());
                JsonNode results = jsonNode.get("results");

                results.forEach(this::reconstructJson);

                return objectMapper.treeToValue(results, classType);
            } else {
                logger.error("GET request {} failed. Status code: {}", uri, response.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Failed to retrieve games: {} {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }

    @Override
    public Set<GameDTO> getAllGamesWithDetails(Set<GameDTO> gameDTOSet) {
        Set<GameDTO> allGamesWithDetails = new HashSet<>();

        for (GameDTO gameDTO : gameDTOSet) {
            GameDTO gameDTOWithDetail = getGameById(gameDTO.getId());

            if (gameDTOWithDetail != null) {
                allGamesWithDetails.add(gameDTOWithDetail);
            } else {
                return null;
            }
        }

        return allGamesWithDetails;
    }

    @Override
    public GameDTO getGameById(Long id) {
        String uri = String.format("%s/%s?key=%s", BASE_URL, id, API_KEY);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Accept", "Application/json")
                .uri(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());

                return objectMapper.treeToValue(reconstructJson(jsonNode), GameDTO.class);
            } else {
                logger.error("GET request {} failed. Status code: {}", uri, response.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Failed to retrieve game: {} {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }

    private JsonNode reconstructJson(JsonNode node) {
        ObjectNode objectNode = (ObjectNode) node;
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        objectNode.get("parent_platforms").forEach(parentPlatform -> arrayNode.add(parentPlatform.get("platform")));
        objectNode.remove("parent_platforms");
        objectNode.set("platforms", arrayNode);

        return objectNode;
    }
}
