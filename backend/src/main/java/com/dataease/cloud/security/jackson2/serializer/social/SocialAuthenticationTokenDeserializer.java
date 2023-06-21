package com.dataease.cloud.security.jackson2.serializer.social;

import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.social.SocialAuthenticationToken;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 * 反序列化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 5:04 PM
 **/
public class SocialAuthenticationTokenDeserializer extends JsonDeserializer<SocialAuthenticationToken> {

    private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<List<GrantedAuthority>>() {
    };

    private static final TypeReference<Object> OBJECT = new TypeReference<Object>() {
    };

    @Override
    public SocialAuthenticationToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        Boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
        JsonNode principalNode = readJsonNode(jsonNode, "principal");
        JsonNode authUserNode = readJsonNode(jsonNode, "auth2User");
        String authorizedClientRegistrationId = readJsonNode(jsonNode, "authorizedClientRegistrationId").asText();
        LocalUser principal = getPrincipal(mapper, principalNode);
        Object authUser = getAuthUser(mapper, authUserNode);
        List<GrantedAuthority> authorities = mapper.readValue(readJsonNode(jsonNode, "authorities").traverse(mapper),
                GRANTED_AUTHORITY_LIST);
        SocialAuthenticationToken token = (!authenticated)
                ? null
                : new SocialAuthenticationToken(principal, authUser, authorizedClientRegistrationId);
        JsonNode detailsNode = readJsonNode(jsonNode, "details");
        if (detailsNode.isNull() || detailsNode.isMissingNode()) {
            token.setDetails(null);
        } else {
            Object details = mapper.readValue(detailsNode.toString(), OBJECT);
            token.setDetails(details);
        }
        return token;
    }

    private Object getCredentials(JsonNode credentialsNode) {
        if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
            return null;
        }
        return credentialsNode.asText();
    }

    private LocalUser getPrincipal(ObjectMapper mapper, JsonNode principalNode)
            throws IOException, JsonParseException, JsonMappingException {
        if (principalNode.isObject()) {
            return mapper.readValue(principalNode.traverse(mapper), LocalUser.class);
        }
        return null;
    }

    private Object getAuthUser(ObjectMapper mapper, JsonNode authUserNode)
            throws IOException, JsonParseException, JsonMappingException {
        if (authUserNode.isObject()) {
            return mapper.readValue(authUserNode.traverse(mapper), Object.class);
        }
        return authUserNode.asText();
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
