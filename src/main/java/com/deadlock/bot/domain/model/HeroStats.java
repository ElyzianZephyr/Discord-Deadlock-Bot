package com.deadlock.bot.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Доменная модель статистики игрока на конкретном герое.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeroStats {

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("hero_id")
    private int heroId;

    @JsonProperty("kills")
    private int kills;

    @JsonProperty("deaths")
    private int deaths;

    @JsonProperty("assists")
    private int assists;

    @JsonProperty("matches_played")
    private int matchesPlayed;

    @JsonProperty("wins")
    private int wins;

    @JsonProperty("time_played")
    private int timePlayed;

    // Бизнес-логика форматирования остается в доменной модели
    public String toSimpleString() {
        double winRate = matchesPlayed > 0 ? (wins * 100.0 / matchesPlayed) : 0;
        int minutesPlayed = timePlayed / 60;
        double kda = deaths > 0 ? (double)(kills + assists) / deaths : (kills + assists);

        return String.format(
                "Герой ID: %d | Матчей: %d | Побед: %d (%.1f%%) | K/D/A: %d/%d/%d (%.2f) | Время: %d мин",
                heroId, matchesPlayed, wins, winRate, kills, deaths, assists, kda, minutesPlayed
        );
    }
}