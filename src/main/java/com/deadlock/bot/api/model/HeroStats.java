package com.deadlock.bot.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    // Геттеры
    public int getAccountId() { return accountId; }
    public int getHeroId() { return heroId; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getAssists() { return assists; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getTimePlayed() { return timePlayed; }

    // Сеттеры
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public void setHeroId(int heroId) { this.heroId = heroId; }
    public void setKills(int kills) { this.kills = kills; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setAssists(int assists) { this.assists = assists; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public void setWins(int wins) { this.wins = wins; }
    public void setTimePlayed(int timePlayed) { this.timePlayed = timePlayed; }


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