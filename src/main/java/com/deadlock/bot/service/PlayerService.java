package com.deadlock.bot.service;

import com.deadlock.bot.domain.exception.InvalidInputException;
import com.deadlock.bot.domain.exception.ServiceException;
import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.port.DeadlockApiPort;
import com.deadlock.bot.domain.model.MatchHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.Objects;

/**
 * Сервис бизнес-логики.
 * Отвечает за проверку ввода, вызов API и формирование ответа.
 */
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String ERROR_ACCOUNT_ID_REQUIRED = "Укажите account_id";
    private static final String ERROR_ACCOUNT_ID_NUMERIC = "Account_id должен быть числом";
    private static final String ERROR_SEARCH_QUERY_REQUIRED = "Укажите ник или Steam ID для поиска.";

    private final DeadlockApiPort apiPort;
    private final Duration timeout;

    /**
     * Конструктор с таймаутом по умолчанию.
     */
    public PlayerService(DeadlockApiPort apiPort) {
        this(apiPort, DEFAULT_TIMEOUT);
    }

    /**
     * Конструктор с возможностью задания кастомного таймаута.
     */
    public PlayerService(DeadlockApiPort apiPort, Duration timeout) {
        this.apiPort = Objects.requireNonNull(apiPort, "apiPort не может быть null");
        this.timeout = Objects.requireNonNull(timeout, "timeout не может быть null");
    }

    /**
     * Поиск профиля Steam.
     * Возвращает Optional.empty(), если профиль не найден.
     *
     * @param searchQuery ник или Steam ID для поиска
     * @return CompletableFuture с Optional профиля Steam
     */
    public CompletableFuture<Optional<SteamProfile>> getSteamProfile(String searchQuery) {
        logger.debug("Запрос на поиск профиля Steam: {}", searchQuery);

        return validateSearchQuery(searchQuery)
                .thenCompose(this::executeProfileSearch)
                .exceptionally(this::handleProfileSearchError);
    }

    /**
     * Получение истории матчей игрока.
     * Возвращает чистый список для дальнейшей отрисовки.
     *
     * @param accountIdStr идентификатор аккаунта в виде строки
     * @return CompletableFuture со списком матчей
     */
    public CompletableFuture<List<MatchHistory>> getMatchHistory(String accountIdStr) {
        logger.debug("Запрос истории матчей для account_id: {}", accountIdStr);

        return executeWithAccountId(accountIdStr, apiPort::getMatchHistory)
                .exceptionally(throwable -> handleListError(throwable, "истории матчей"));
    }

    /**
     * Получение статистики игрока по героям.
     * Возвращает чистый список для дальнейшей отрисовки.
     *
     * @param accountIdStr идентификатор аккаунта в виде строки
     * @return CompletableFuture со статистикой по героям
     */
    public CompletableFuture<List<HeroStats>> getPlayerStats(String accountIdStr) {
        logger.debug("Запрос статистики по героям для account_id: {}", accountIdStr);

        return executeWithAccountId(accountIdStr, apiPort::getHeroStats)
                .exceptionally(throwable -> handleListError(throwable, "статистики по героям"));
    }

    /**
     * Валидация поискового запроса.
     */
    private CompletableFuture<String> validateSearchQuery(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            logger.warn("Пустой поисковый запрос");
            return CompletableFuture.failedFuture(
                    new InvalidInputException(ERROR_SEARCH_QUERY_REQUIRED)
            );
        }

        String trimmedQuery = searchQuery.trim();
        logger.debug("Поисковый запрос после валидации: {}", trimmedQuery);
        return CompletableFuture.completedFuture(trimmedQuery);
    }

    /**
     * Выполнение поиска профиля с таймаутом.
     */
    private CompletableFuture<Optional<SteamProfile>> executeProfileSearch(String searchQuery) {
        return apiPort.searchSteamProfile(searchQuery)
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .thenApply(profiles -> {
                    logger.debug("Получено {} профилей для запроса: {}", profiles.size(), searchQuery);

                    if (profiles.isEmpty()) {
                        logger.info("Профили не найдены для запроса: {}", searchQuery);
                        return Optional.empty();
                    }

                    SteamProfile firstProfile = profiles.get(0);
                    logger.info("Найден профиль: {} (ID: {})",
                            firstProfile.getPersonaName(), firstProfile.getAccountId());
                    return Optional.of(firstProfile);
                });
    }

    /**
     * Обобщенный метод для выполнения операций с account_id.
     */
    private <T> CompletableFuture<T> executeWithAccountId(
            String accountIdStr, Function<Integer, CompletableFuture<T>> operation
    ) {
        return parseAccountId(accountIdStr)
                .thenCompose(accountId -> {
                    logger.debug("Выполнение операции для account_id: {}", accountId);
                    return operation.apply(accountId)
                            .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
                });
    }

    /**
     * Парсинг и валидация account_id.
     */
    private CompletableFuture<Integer> parseAccountId(String accountIdStr) {
        if (accountIdStr == null || accountIdStr.isBlank()) {
            logger.warn("Попытка вызова с пустым account_id");
            return CompletableFuture.failedFuture(
                    new InvalidInputException(ERROR_ACCOUNT_ID_REQUIRED)
            );
        }

        try {
            int accountId = Integer.parseInt(accountIdStr.trim());

            // Дополнительная валидация для Steam account_id
            if (accountId <= 0) {
                logger.warn("account_id должен быть положительным: {}", accountId);
                return CompletableFuture.failedFuture(
                        new InvalidInputException("Account_id должен быть положительным числом")
                );
            }

            logger.debug("Успешный парсинг account_id: {}", accountId);
            return CompletableFuture.completedFuture(accountId);

        } catch (NumberFormatException e) {
            logger.error("Ошибка парсинга account_id: {}", accountIdStr, e);
            return CompletableFuture.failedFuture(
                    new InvalidInputException(ERROR_ACCOUNT_ID_NUMERIC)
            );
        }
    }

    /**
     * Обработка ошибок при поиске профиля.
     */
    private Optional<SteamProfile> handleProfileSearchError(Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            logger.error("Таймаут при поиске профиля Steam");
            throw new ServiceException("Превышено время ожидания ответа от Steam API");
        }

        if (throwable instanceof InvalidInputException) {
            logger.error("Ошибка валидации при поиске профиля: {}", throwable.getMessage());
            throw (InvalidInputException) throwable;
        }

        logger.error("Неизвестная ошибка при поиске профиля Steam", throwable);
        throw new ServiceException("Ошибка при поиске профиля Steam");
    }

    /**
     * Обработка ошибок для методов, возвращающих список.
     */
    private <T> List<T> handleListError(Throwable throwable, String operationName) {
        if (throwable instanceof TimeoutException) {
            logger.error("Таймаут при получении {}", operationName);
            throw new ServiceException("Превышено время ожидания ответа от сервиса");
        }

        if (throwable instanceof InvalidInputException) {
            logger.error("Ошибка валидации при получении {}: {}", operationName, throwable.getMessage());
            throw (InvalidInputException) throwable;
        }

        logger.error("Неизвестная ошибка при получении {}", operationName, throwable);
        throw new ServiceException("Ошибка получения данных от сервиса");
    }
}