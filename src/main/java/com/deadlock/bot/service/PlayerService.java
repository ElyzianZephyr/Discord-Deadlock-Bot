package com.deadlock.bot.service;

import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.port.DeadlockApiPort;
import com.deadlock.bot.domain.model.MatchHistory;

import java.util.Optional;
import java.util.List;

/**
 * Сервис бизнес-логики.
 * Отвечает за проверку ввода, вызов API и формирование ответа.
 */
public class PlayerService {

    private final DeadlockApiPort apiPort;

    // Внедрение зависимости через интерфейс (Clean Architecture)
    public PlayerService(DeadlockApiPort apiPort) {
        this.apiPort = apiPort;
    }

    /**
     * Поиск профиля Steam.
     * Возвращает Optional.empty(), если профиль не найден.
     */
    public Optional<SteamProfile> getSteamProfile(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            throw new IllegalArgumentException("Укажите ник или Steam ID для поиска.");
        }

        List<SteamProfile> profiles = apiPort.searchSteamProfile(searchQuery.trim());

        if (profiles.isEmpty()) {
            return Optional.empty();
        }

        // Возвращаем первый найденный профиль в виде объекта
        return Optional.of(profiles.get(0));
    }

    /**
     * Получение истории матчей игрока.
     * Возвращает чистый список для дальнейшей отрисовки.
     */
    public List<MatchHistory> getMatchHistory(String accountIdStr) {
        if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Укажите account_id");
        }

        try {
            int accountId = Integer.parseInt(accountIdStr.trim());
            return apiPort.getMatchHistory(accountId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account_id должен быть числом");
        }
    }

    /**
     * Получение статистики игрока по героям.
     * Возвращает чистый список для дальнейшей отрисовки.
     */
    public List<HeroStats> getPlayerStats(String accountIdStr) {
        if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Укажите account_id");
        }

        try {
            int accountId = Integer.parseInt(accountIdStr.trim());
            return apiPort.getHeroStats(accountId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account_id должен быть числом");
        }
    }
}