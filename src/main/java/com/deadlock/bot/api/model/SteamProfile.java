package com.deadlock.bot.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamProfile {

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("personaname")
    private String personaName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("avatarfull")
    private String avatarFull;

    @JsonProperty("avatarmedium")
    private String avatarMedium;

    @JsonProperty("profileurl")
    private String profileUrl;

    // Геттеры и сеттеры
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarFull() {
        return avatarFull;
    }

    public void setAvatarFull(String avatarFull) {
        this.avatarFull = avatarFull;
    }

    public String getAvatarMedium() {
        return avatarMedium;
    }

    public void setAvatarMedium(String avatarMedium) {
        this.avatarMedium = avatarMedium;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    // Метод для форматированного вывода
    public String toSimpleString() {
        return String.format("ID: %d Steam Name: %s", accountId, personaName);
    }
    // Метод для получения SteamID в формате 64-bit (если нужно)
    public long getSteamId64() {
        // Steam ID 64-bit формируется как 76561197960265728 + account_id
        return 76561197960265728L + accountId;
    }

    // Метод для получения ссылки на профиль Steam
    public String getSteamProfileLink() {
        return "https://steamcommunity.com/profiles/" + getSteamId64();
    }

    // Расширенный метод для вывода
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("**Steam Profile**\n");
        sb.append("┌ ID: ").append(accountId).append("\n");
        sb.append("├ Имя: ").append(personaName).append("\n");
        sb.append("├ SteamID64: ").append(getSteamId64()).append("\n");
        sb.append("└ Ссылка: ").append(getSteamProfileLink());
        return sb.toString();
    }

}

