package com.deadlock.bot.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Доменная модель истории матча.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchHistory {

    @JsonProperty("match_id")
    private long matchId;

    @JsonProperty("account_id")
    private int accountId;

    @JsonProperty("hero_id")
    private int heroId;

    @JsonProperty("player_kills")
    private int kills;

    @JsonProperty("player_deaths")
    private int deaths;

    @JsonProperty("player_assists")
    private int assists;

    @JsonProperty("match_duration_s")
    private int matchDurationSeconds;

    @JsonProperty("match_result")
    private int matchResult;

    @JsonProperty("player_team")
    private int playerTeam;

    @JsonProperty("start_time")
    private long startTime;

    // --- Бизнес-логика модели ---

    /**
     * Определение победы.
     * Обычно в API от Valve match_result совпадает с номером победившей команды.
     */
    public boolean isWin() {
        return matchResult == playerTeam;
    }

    /**
     * Форматирование длительности матча в MM:SS
     */
    public String getFormattedDuration() {
        int minutes = matchDurationSeconds / 60;
        int seconds = matchDurationSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Подсчет KDA
     */
    public double getKda() {
        if (deaths == 0) {
            return kills + assists;
        }
        return (double) (kills + assists) / deaths;
    }
}