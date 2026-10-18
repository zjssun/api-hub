package com.main.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ProPlayerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ProPlayerRegistry.class);

    private final Map<String, String> nameByPlayerId;

    public ProPlayerRegistry() {
        Map<String, String> map = new HashMap<>();
        try (InputStream inputStream = new ClassPathResource("pro-players.json").getInputStream()) {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONArray players = JSON.parseArray(json);
            for (int i = 0; i < players.size(); i++) {
                JSONObject player = players.getJSONObject(i);
                if (player == null) {
                    continue;
                }
                String uuid = player.getString("uuid");
                String name = player.getString("name");
                if (uuid != null && !uuid.isBlank() && name != null && !name.isBlank()) {
                    map.put(uuid, name);
                }
            }
            logger.info("已加载 {} 名职业选手注册信息", map.size());
        } catch (Exception e) {
            logger.error("加载 pro-players.json 失败，职业选手识别功能不可用", e);
        }
        this.nameByPlayerId = Collections.unmodifiableMap(map);
    }

    public Optional<String> displayNameOf(String playerId) {
        if (playerId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(nameByPlayerId.get(playerId));
    }
}
