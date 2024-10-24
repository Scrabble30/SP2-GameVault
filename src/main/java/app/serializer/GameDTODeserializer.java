package app.serializer;

import app.dto.GameDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class GameDTODeserializer extends JsonDeserializer<GameDTO> {

    @Override
    public GameDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = reconstruct(oc.readTree(jp));

        DeserializationConfig config = ctxt.getConfig();
        JavaType type = config.constructType(GameDTO.class);
        JsonDeserializer<Object> defaultDeserializer = BeanDeserializerFactory.instance.buildBeanDeserializer(ctxt, type, config.introspect(type));

        if (defaultDeserializer instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
        }

        JsonParser treeParser = oc.treeAsTokens(node);
        config.initialize(treeParser);

        if (treeParser.getCurrentToken() == null) {
            treeParser.nextToken();
        }

        return (GameDTO) defaultDeserializer.deserialize(treeParser, ctxt);
    }

    private JsonNode reconstruct(JsonNode node) {
        if (!node.has("parent_platforms")) {
            return node;
        }

        ObjectNode objectNode = (ObjectNode) node;
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();

        objectNode.get("parent_platforms").forEach(parentPlatform -> arrayNode.add(parentPlatform.get("platform")));
        objectNode.remove("parent_platforms");
        objectNode.set("platforms", arrayNode);

        return objectNode;
    }
}
