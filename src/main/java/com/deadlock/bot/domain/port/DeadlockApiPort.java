package com.deadlock.bot.domain.port;

import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.model.MatchHistory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Порт для взаимодействия с внешним API Deadlock.
 * Ядро приложения (Domain/UseCase) зависит только от этого интерфейса.
 */
public interface DeadlockApiPort {

    /**
     * Поиск профиля по текстовому запросу (имя или SteamID)
     */
    CompletableFuture<List<SteamProfile>> searchSteamProfile(String query);

    /**
     * Получение конкретного профиля по account_id
     */
    CompletableFuture<Optional<SteamProfile>> getPlayerProfile(int accountId);

    /**
     * Получение истории матчей для конкретного игрока
     */
    CompletableFuture<List<MatchHistory>> getMatchHistory(int accountId);

    /**
     * Получение статистики по героям для конкретного игрока
     */
    CompletableFuture<List<HeroStats>> getHeroStats(int accountId);
}