package com.anirudhgupta109.hytalediscordlink;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountManager {

    private final File linkedAccountsFile;
    private final Map<UUID, String> linkedAccounts = new HashMap<>();
    private final Map<String, UUID> linkCodes = new HashMap<>();

    public AccountManager(File dataDirectory) {
        this.linkedAccountsFile = new File(dataDirectory, "linked-accounts.json");
        load();
    }

    public String generateLinkCode(UUID playerUUID) {
        String code = String.format("%06d", (int) (Math.random() * 1000000));
        linkCodes.put(code, playerUUID);
        return code;
    }

    public UUID getPlayerUUIDByCode(String code) {
        return linkCodes.get(code);
    }

    public void linkAccount(UUID playerUUID, String discordId) {
        linkedAccounts.put(playerUUID, discordId);
        save();
    }

    public boolean isLinked(UUID playerUUID) {
        return linkedAccounts.containsKey(playerUUID);
    }

    public String getDiscordId(UUID playerUUID) {
        return linkedAccounts.get(playerUUID);
    }

    private void load() {
        if (!linkedAccountsFile.exists() || linkedAccountsFile.length() == 0) {
            return;
        }
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(linkedAccountsFile)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            for (Object key : jsonObject.keySet()) {
                UUID playerUUID = UUID.fromString((String) key);
                String discordId = (String) jsonObject.get(key);
                linkedAccounts.put(playerUUID, discordId);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<UUID, String> entry : linkedAccounts.entrySet()) {
            jsonObject.put(entry.getKey().toString(), entry.getValue());
        }
        try (FileWriter writer = new FileWriter(linkedAccountsFile)) {
            writer.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
