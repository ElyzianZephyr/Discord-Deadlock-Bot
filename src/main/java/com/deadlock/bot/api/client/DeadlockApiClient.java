package com.deadlock.bot.api.client;

import com.deadlock.bot.api.model.HeroStats;
import com.deadlock.bot.api.model.SteamProfile;
import com.fasterxml.jackson.databind.JavaType;
import okhttp3.OkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class DeadlockApiClient {
    private static final String BASE_URL = "https://api.deadlock-api.com/v1";
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;

    public DeadlockApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Добавляем метод для проверки доступности API
    public boolean isApiAvailable() {
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(BASE_URL + "/health")  // предположительный эндпоинт
                    .head()
                    .build();

            try (okhttp3.Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public SteamProfile getPlayerProfile(String steamId) throws Exception {
        String url = BASE_URL + "/players/" + steamId;

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get player profile: " + response.code());
            }

            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, SteamProfile.class);
        }
    }

    public List<SteamProfile> searchSteamProfile(String searchQuery) throws Exception {
        String url = BASE_URL + "/players/steam-search?search_query=" + searchQuery;

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return new ArrayList<>(); // Пустой список если ничего не найдено
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to search steam profiles: " + response.code());
            }

            String responseBody = response.body().string();

            // API возвращает массив объектов SteamProfile
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, SteamProfile.class);
            return objectMapper.readValue(responseBody, type);
        }
    }

    public List<HeroStats> getHeroStats(int accountId) throws Exception {
        String url = BASE_URL + "/players/hero-stats?account_ids=" + accountId;

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return new ArrayList<>(); // Статистика не найдена
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get hero stats: " + response.code());
            }

            String responseBody = response.body().string();

            // Проверяем на пустой ответ
            if (responseBody == null || responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                return new ArrayList<>();
            }

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, HeroStats.class);
            return objectMapper.readValue(responseBody, type);
        }
    }
}