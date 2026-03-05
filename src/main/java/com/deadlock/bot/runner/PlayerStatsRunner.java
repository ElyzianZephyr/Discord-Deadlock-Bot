package com.deadlock.bot.runner;

import com.deadlock.bot.adapter.api.client.DeadlockApiClient;
import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerStatsRunner extends ListenerAdapter {

    private final DeadlockApiClient apiClient;

    public PlayerStatsRunner() {
        this.apiClient = new DeadlockApiClient();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("!steam ")) {
            String searchQuery = message.substring(7).trim();

            if (searchQuery.isEmpty()) {
                event.getChannel().sendMessage("Укажите ник или Steam ID").queue();
                return;
            }

            try {
                List<SteamProfile> profiles = apiClient.searchSteamProfile(searchQuery);

                if (profiles.isEmpty()) {
                    event.getChannel().sendMessage("Профиль не найден").queue();
                    return;
                }

                SteamProfile profile = profiles.get(0);
                event.getChannel().sendMessage(profile.toDetailedString()).queue();

            } catch (Exception e) {
                event.getChannel().sendMessage("Ошибка: " + e.getMessage()).queue();
            }
        }




        // Новая команда для статистики
        if (message.startsWith("!stats ")) {
            String accountIdStr = message.substring(7).trim();

            if (accountIdStr.isEmpty()) {
                event.getChannel().sendMessage("Укажите account_id").queue();
                return;
            }

            try {
                int accountId = Integer.parseInt(accountIdStr);
                List<HeroStats> stats = apiClient.getHeroStats(accountId);

                if (stats.isEmpty()) {
                    event.getChannel().sendMessage("Статистика не найдена").queue();
                    return;
                }

                // Формируем сообщение со статистикой
                StringBuilder response = new StringBuilder();
                response.append("**Статистика игрока ").append(accountId).append(":**\n\n");

                // Показываем топ-5 героев по количеству матчей
                stats.stream()
                        .limit(5)
                        .forEach(hero -> response.append(hero.toSimpleString()).append("\n"));

                if (stats.size() > 5) {
                    response.append("\n*... и еще ").append(stats.size() - 5).append(" героев*");
                }

                event.getChannel().sendMessage(response.toString()).queue();

            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Account_id должен быть числом").queue();
            } catch (Exception e) {
                event.getChannel().sendMessage("Ошибка: " + e.getMessage()).queue();
            }
        }


    }

    private String getSteamIdFromInput(String input) {
        // Проверяем, является ли ввод числом (Steam ID)
        if (input.matches("\\d+")) {
            return input; // Это уже Steam ID
        }

        // Если это ник, то пока возвращаем null
        // В следующих шагах добавим логику преобразования ника в Steam ID
        return null;
    }
}