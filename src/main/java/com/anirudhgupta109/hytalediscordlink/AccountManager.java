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
    private final File accountsFile;
    private final Map<UUID, String> linkCodes = new HashMap<>();
    private DiscordBot discordBot; // Added DiscordBot dependency

    public AccountManager(File dataDirectory) {
        this.linkedAccountsFile = new File(dataDirectory, "linked-accounts.json");
        this.accountsFile = new File(dataDirectory, "accounts.json");
        load();
        loadAccounts();
    }

    // Setter for DiscordBot, to be called after DiscordBot is initialized
    public void setDiscordBot(DiscordBot discordBot) {
        this.discordBot = discordBot;
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
        addActiveSession(playerUUID, discordId);
        save();
        if (discordBot != null && discordBot.getConfig().isRoleSyncEnabled()) {
            discordBot.syncDiscordRolesToHytaleGroups(playerUUID, discordId);
        }
    }

    public boolean isLinked(UUID playerUUID) {
        return linkedAccounts.containsKey(playerUUID);
    }

    public String getDiscordId(UUID playerUUID) {
        return linkedAccounts.get(playerUUID);
    }

    public void addActiveSession(UUID playerUUID, String discordId) {
        accounts.put(playerUUID, discordId);
        saveAccounts();
    }

    public void removeActiveSession(UUID playerUUID) {
        accounts.remove(playerUUID);
        saveAccounts();
    }

    public boolean isLoggedIn(UUID playerUUID) {
        return accounts.containsKey(playerUUID);
    }

    public void removeFullAccount(UUID playerUUID) {
        if (discordBot != null && discordBot.getConfig().isRoleSyncEnabled()) {
            discordBot.clearSyncedHytaleGroups(playerUUID);
        }
        linkedAccounts.remove(playerUUID);
        accounts.remove(playerUUID);
        save();
        saveAccounts();
    }

    public UUID getPlayerUUIDByDiscordId(String discordId) {
        for (Map.Entry<UUID, String> entry : accounts.entrySet()) {
            if (entry.getValue().equals(discordId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void loadAccounts() {
        if (!accountsFile.exists() || accountsFile.length() == 0) {
            return;
        }
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(accountsFile)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            for (Object key : jsonObject.keySet()) {
                UUID playerUUID = UUID.fromString((String) key);
                String discordId = (String) jsonObject.get(key);
                accounts.put(playerUUID, discordId);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveAccounts() {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<UUID, String> entry : accounts.entrySet()) {
            jsonObject.put(entry.getKey().toString(), entry.getValue());
        }
        try (FileWriter writer = new FileWriter(accountsFile)) {
            writer.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
