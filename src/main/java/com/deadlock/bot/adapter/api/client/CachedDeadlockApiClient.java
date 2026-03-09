package com.deadlock.bot.adapter.api.client;

import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.MatchHistory;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.port.DeadlockApiPort;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CachedDeadlockApiClient implements DeadlockApiPort {

    private final DeadlockApiPort delegate;
    private final Cache<String, CompletableFuture<List<SteamProfile>>> searchCache;
    private final Cache<Integer, CompletableFuture<Optional<SteamProfile>>> profileCache;
    private final Cache<Integer, CompletableFuture<List<MatchHistory>>> matchCache;
    private final Cache<Integer, CompletableFuture<List<HeroStats>>> statsCache;



    public CachedDeadlockApiClient(DeadlockApiPort delegate) {
        this.delegate = delegate;
        this.searchCache = buildCache();
        this.profileCache = buildCache();
        this.matchCache = buildCache();
        this.statsCache = buildCache();
    }

    private <K, V> Cache<K, V> buildCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }


    @Override
    public CompletableFuture<List<SteamProfile>> searchSteamProfile(String query) {
        return withCache(searchCache, query, () -> delegate.searchSteamProfile(query));
    }


    @Override
    public CompletableFuture<Optional<SteamProfile>> getPlayerProfile(int accountId) {
        return  withCache(profileCache, accountId, () -> delegate.getPlayerProfile(accountId));
    }

    @Override
    public CompletableFuture<List<MatchHistory>> getMatchHistory(int accountId) {
        return withCache(matchCache, accountId, () -> delegate.getMatchHistory(accountId));
    }

    @Override
    public CompletableFuture<List<HeroStats>> getHeroStats(int accountId) {
        return withCache(statsCache, accountId, () -> delegate.getHeroStats(accountId));
    }


    /**
     * Универсальный метод для работы с любым кэшем.
     * * @param cache кэш, в котором ищем данные
     * @param key ключ (строка или число)
     * @param delegateCall действие, которое нужно выполнить, если кэш пуст
     */
    private <K, V> CompletableFuture<V> withCache(
            Cache<K, CompletableFuture<V>> cache,
            K key,
            Supplier<CompletableFuture<V>> delegateCall) {

        CompletableFuture<V> future = cache.getIfPresent(key);

        if (future == null) {
            future = delegateCall.get(); // Вызываем то действие, которое нам передали
            cache.put(key, future);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    cache.invalidate(key);
                }
            });
        }

        return future;
    }
}
