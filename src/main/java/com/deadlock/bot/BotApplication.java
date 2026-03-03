package com.deadlock.bot;

import com.deadlock.bot.runner.PlayerStatsRunner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotApplication {
    private static JDA jda;
    private static PlayerStatsRunner playerStatsRunner;

    public static void main(String[] args) {
        try {
            // Получаем токен из переменной окружения
            String token = System.getenv("DISCORD_BOT_TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("DISCORD_BOT_TOKEN environment variable is not set");
            }

            // Инициализация JDA
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .build();

            // Ожидаем завершения инициализации
            jda.awaitReady();

            // Регистрация обработчика команд
            PlayerStatsRunner playerStatsRunner = new PlayerStatsRunner();
            jda.addEventListener(playerStatsRunner);

            System.out.println("Bot started successfully!");


        } catch (Exception e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}