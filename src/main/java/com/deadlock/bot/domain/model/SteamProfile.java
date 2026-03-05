package com.deadlock.bot.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Доменная модель профиля Steam.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    // Метод для форматированного вывода
    public String toSimpleString() {
        return String.format("ID: %d Steam Name: %s", accountId, personaName);
    }

    // Метод для получения SteamID в формате 64-bit
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
        return "**Steam Profile**\n" +
                "┌ ID: " + accountId + "\n" +
                "├ Имя: " + personaName + "\n" +
                "├ SteamID64: " + getSteamId64() + "\n" +
                "└ Ссылка: " + getSteamProfileLink();
    }
}