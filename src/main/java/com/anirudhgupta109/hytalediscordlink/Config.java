package com.anirudhgupta109.hytalediscordlink;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Config {

    private final String botToken;
    private final String guildId;
    private final String channelId;
    private final boolean authEnabled;

    public Config(String botToken, String guildId, String channelId, boolean authEnabled) {
        this.botToken = botToken;
        this.guildId = guildId;
        this.channelId = channelId;
        this.authEnabled = authEnabled;
    }

    public static Config load(File file) {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(file)) {
            Map<String, Object> data = yaml.load(in);
            String botToken = (String) data.get("bot-token");
            String guildId = (String) data.get("guild-id");
            String channelId = (String) data.get("channel-id");
            boolean authEnabled = (boolean) data.get("auth-enabled");
            return new Config(botToken, guildId, channelId, authEnabled);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBotToken() {
        return botToken;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }
}
