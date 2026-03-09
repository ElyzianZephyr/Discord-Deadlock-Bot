package com.deadlock.bot.adapter.api.client;

import com.deadlock.bot.domain.exception.ApiException;
import com.deadlock.bot.domain.model.MatchHistory;
import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.domain.port.DeadlockApiPort;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DeadlockApiClient implements DeadlockApiPort {
    private static final String BASE_URL = "https://api.deadlock-api.com/v1";
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;

    public DeadlockApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CompletableFuture<List<SteamProfile>> searchSteamProfile(String searchQuery) {
        String url = BASE_URL + "/players/steam-search?search_query=" + searchQuery;
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, SteamProfile.class);
        return executeGetRequest(url, type , new ArrayList<>());
    }



    @Override
    public CompletableFuture<Optional<SteamProfile>> getPlayerProfile(int accountId) {
        String url = BASE_URL + "/players/" + accountId;
        JavaType type = objectMapper.getTypeFactory().constructType(SteamProfile.class);


        return this.<SteamProfile>executeGetRequest(url, type, null)
                .thenApply(Optional::ofNullable);
    }



    @Override
    public CompletableFuture<List<MatchHistory>> getMatchHistory(int accountId) {
        String url = BASE_URL + "/players/" + accountId + "/match-history";
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, MatchHistory.class);
        return executeGetRequest(url, type, new ArrayList<>());
    }

    @Override
    public CompletableFuture<List<HeroStats>> getHeroStats(int accountId) {
        String url = BASE_URL + "/players/hero-stats?account_ids=" + accountId;
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, HeroStats.class);
        return executeGetRequest(url, type, new ArrayList<>());
    }

    /// Вспомогательные методы

    private <T> CompletableFuture<T> executeGetRequest(String url, JavaType type, T valueIfNotFound) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        httpClient.newCall(request).enqueue(createCallback(future, type, valueIfNotFound));

        return future;
    }

    private <T> okhttp3.Callback createCallback(CompletableFuture<T> future, JavaType type, T valueIfNotFound) {
        return new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                future.completeExceptionally(new ApiException("Network error: " + e.getMessage()));
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) {

                try (response) {
                    if (response.code() == 404) {
                        future.complete(valueIfNotFound);
                        return;
                    }

                    if (!response.isSuccessful() || response.body() == null) {
                        future.completeExceptionally(new ApiException("Failed to search. HTTP: " + response.code()));
                        return;
                    }

                    //String responseBody = response.body().string();
                    //T result = objectMapper.readValue(responseBody, type);
                    T result = objectMapper.readValue(response.body().charStream(), type);

                    // Передаем готовые данные
                    future.complete(result);

                } catch (Exception e) {
                    // Перехватываем ошибки парсинга JSON
                    future.completeExceptionally(new ApiException("Error parsing response: " + e.getMessage()));
                }
            }
        };
    }
}