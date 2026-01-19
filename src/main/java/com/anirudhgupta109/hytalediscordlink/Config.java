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
    private final Map<String, String> channels;
    private final boolean authEnabled;
    private final boolean strictAuth;
    private final boolean roleSyncEnabled;
    private final Map<String, String> discordRoleToHytaleGroupMap;

    public Config(String botToken, String guildId, Map<String, String> channels, boolean authEnabled, boolean strictAuth, boolean roleSyncEnabled, Map<String, String> discordRoleToHytaleGroupMap) {
        this.botToken = botToken;
        this.guildId = guildId;
        this.channels = channels;
        this.authEnabled = authEnabled;
        this.strictAuth = strictAuth;
        this.roleSyncEnabled = roleSyncEnabled;
        this.discordRoleToHytaleGroupMap = discordRoleToHytaleGroupMap;
    }

    public static Config load(File file) {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(file)) {
            Map<String, Object> data = yaml.load(in);
            String botToken = (String) data.get("bot-token");
            String guildId = (String) data.get("guild-id");
            Map<String, String> channels = (Map<String, String>) data.get("channels");
            boolean authEnabled = (boolean) data.get("auth-enabled");
            boolean strictAuth = (boolean) data.getOrDefault("strict-auth", false);
            boolean roleSyncEnabled = (boolean) data.getOrDefault("role-sync-enabled", false);
            Map<String, String> discordRoleToHytaleGroupMap = (Map<String, String>) data.getOrDefault("discord-role-to-hytale-group-map", new HashMap<>());
            return new Config(botToken, guildId, channels, authEnabled, strictAuth, roleSyncEnabled, discordRoleToHytaleGroupMap);
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

    public Map<String, String> getChannels() {
        return channels;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public boolean isStrictAuth() {
        return strictAuth;
    }

    public boolean isRoleSyncEnabled() {
        return roleSyncEnabled;
    }

    public Map<String, String> getDiscordRoleToHytaleGroupMap() {
        return discordRoleToHytaleGroupMap;
    }
}
