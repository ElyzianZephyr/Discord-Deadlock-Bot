package com.deadlock.bot.adapter.api.client;

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
    public List<SteamProfile> searchSteamProfile(String searchQuery) {
        String url = BASE_URL + "/players/steam-search?search_query=" + searchQuery;

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return new ArrayList<>(); // Пустой список если ничего не найдено
            }

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Failed to search steam profiles. HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, SteamProfile.class);
            return objectMapper.readValue(responseBody, type);

        } catch (IOException e) {
            throw new RuntimeException("Network error while searching steam profile", e);
        }
    }

    @Override
    public Optional<SteamProfile> getPlayerProfile(int accountId) {
        String url = BASE_URL + "/players/" + accountId;

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return Optional.empty(); // Возвращаем пустой Optional, если профиль не найден
            }

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Failed to get player profile. HTTP: " + response.code());
            }

            String responseBody = response.body().string();
            return Optional.ofNullable(objectMapper.readValue(responseBody, SteamProfile.class));

        } catch (IOException e) {
            throw new RuntimeException("Network error while getting player profile", e);
        }
    }

    @Override
    public List<MatchHistory> getMatchHistory(int accountId) {
        // Используем базовый эндпоинт без параметров, чтобы получить кэшированную + новую историю
        String url = BASE_URL + "/players/" + accountId + "/match-history";

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return new ArrayList<>(); // История не найдена
            }

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Failed to get match history. HTTP: " + response.code());
            }

            String responseBody = response.body().string();

            if (responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                return new ArrayList<>();
            }

            // Конвертируем JSON-ответ в список объектов MatchHistory
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, MatchHistory.class);
            return objectMapper.readValue(responseBody, type);

        } catch (IOException e) {
            throw new RuntimeException("Network error while getting match history", e);
        }
    }

    @Override
    public List<HeroStats> getHeroStats(int accountId) {
        String url = BASE_URL + "/players/hero-stats?account_ids=" + accountId;

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return new ArrayList<>(); // Статистика не найдена
            }

            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Failed to get hero stats. HTTP: " + response.code());
            }

            String responseBody = response.body().string();

            if (responseBody.trim().isEmpty() || responseBody.equals("[]")) {
                return new ArrayList<>();
            }

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, HeroStats.class);
            return objectMapper.readValue(responseBody, type);

        } catch (IOException e) {
            throw new RuntimeException("Network error while getting hero stats", e);
        }
    }
}