package com.dataease.cloud.security.jackson2.serializer;

import com.dataease.cloud.constant.LoginType;
import com.dataease.cloud.security.LocalUser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * 反序列化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 5:04 PM
 **/
public class LocalUserDeserializer extends JsonDeserializer<LocalUser> {

    private static final TypeReference<Set<SimpleGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<Set<SimpleGrantedAuthority>>() {
    };

    private static final TypeReference<Date> dateTypeReference = new TypeReference<Date>() {
    };

    @Override
    public LocalUser deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = mapper.readTree(jsonParser);
        Set<SimpleGrantedAuthority> authorities = mapper.convertValue(jsonNode.get("authorities"),
                SIMPLE_GRANTED_AUTHORITY_SET);
        JsonNode passwordNode = readJsonNode(jsonNode, "password");
        String password = passwordNode.asText("");
        boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
        String userId = readJsonNode(jsonNode, "userId").asText();
        String userName = readJsonNode(jsonNode, "userName").asText();
        String nickName = readJsonNode(jsonNode, "nickName").asText();
        Integer gender = readJsonNode(jsonNode, "gender").asInt();
        String phone = readJsonNode(jsonNode, "phone").asText();
        String email = readJsonNode(jsonNode, "email").asText();
        Date createTime = mapper.convertValue(jsonNode.get("createTime"), dateTypeReference);
        Date updateTime = mapper.convertValue(jsonNode.get("updateTime"), dateTypeReference);
        String loginType = readJsonNode(jsonNode, "loginType").asText();
        String language = readJsonNode(jsonNode, "language").asText();
        LocalUser localUser = new LocalUser();
        localUser.setUserId(userId);
        localUser.setUserName(userName);
        localUser.setNickName(nickName);
        localUser.setGender(gender);
        localUser.setPhone(phone);
        localUser.setEmail(email);
        localUser.setCreateTime(createTime);
        localUser.setUpdateTime(updateTime);
        localUser.setLoginType(LoginType.valueOf(loginType));
        localUser.setEnabled(enabled);
        localUser.setPassword(password);
        localUser.setAuthorities(authorities);
        localUser.setLanguage(language);
        return localUser;
    }

    private JsonNode readJsonNode(JsonNode jsonNode, String field) {
        return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
    }
}
