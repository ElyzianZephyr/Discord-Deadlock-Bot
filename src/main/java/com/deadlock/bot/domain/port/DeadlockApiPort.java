package com.deadlock.bot.domain.port;

import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.model.MatchHistory;

import java.util.List;
import java.util.Optional;

/**
 * Порт для взаимодействия с внешним API Deadlock.
 * Ядро приложения (Domain/UseCase) зависит только от этого интерфейса.
 */
public interface DeadlockApiPort {

    /**
     * Поиск профиля по текстовому запросу (имя или SteamID)
     */
    List<SteamProfile> searchSteamProfile(String query);

    /**
     * Получение конкретного профиля по account_id
     */
    Optional<SteamProfile> getPlayerProfile(int accountId);

    /**
     * Получение истории матчей для конкретного игрока
     */
    List<MatchHistory> getMatchHistory(int accountId);

    /**
     * Получение статистики по героям для конкретного игрока
     */
    List<HeroStats> getHeroStats(int accountId);
}