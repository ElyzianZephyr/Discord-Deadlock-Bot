package com.deadlock.bot;

import com.deadlock.bot.adapter.api.client.DeadlockApiClient;
import com.deadlock.bot.adapter.discord.command.MatchesCommand;
import com.deadlock.bot.adapter.discord.command.SlashCommand;
import com.deadlock.bot.adapter.discord.command.StatsCommand;
import com.deadlock.bot.adapter.discord.command.SteamCommand;
import com.deadlock.bot.adapter.discord.listener.CommandManager;
import com.deadlock.bot.domain.port.DeadlockApiPort;
import com.deadlock.bot.service.PlayerService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.deadlock.bot.adapter.discord.command.MatchesCommand;

import java.util.List;

public class BotApplication {

    public static void main(String[] args) {
        try {
            // 1. Получаем токен
            String token = System.getenv("DISCORD_BOT_TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("DISCORD_BOT_TOKEN environment variable is not set");
            }

            // 2. РУЧНОЕ ВНЕДРЕНИЕ ЗАВИСИМОСТЕЙ (Dependency Injection)
            // Инициализируем Адаптер (Порт)
            DeadlockApiPort apiPort = new DeadlockApiClient();

            // Инициализируем Сервис (Бизнес-логику)
            PlayerService playerService = new PlayerService(apiPort);

            // Инициализируем Команды
            SlashCommand steamCommand = new SteamCommand(playerService);
            SlashCommand statsCommand = new StatsCommand(playerService);
            SlashCommand matchesCommand = new MatchesCommand(playerService);

            // Инициализируем Маршрутизатор команд
            CommandManager commandManager = new CommandManager(List.of(steamCommand, statsCommand, matchesCommand));

            // 3. Инициализация JDA
            JDA jda = JDABuilder.createDefault(token)
                    // Для слэш-команд MESSAGE_CONTENT больше не является обязательным,
                    // но мы оставим его на случай, если вы добавите другие фичи
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(commandManager) // Подключаем наш новый слушатель
                    .build();

            jda.awaitReady();

            // 4. РЕГИСТРАЦИЯ КОМАНД В DISCORD
            List<SlashCommandData> commandDataList = commandManager.getRegisteredCommands().stream()
                    .map(cmd -> Commands.slash(cmd.getName(), cmd.getDescription())
                            .addOptions(cmd.getOptions()))
                    .toList();

            // JDA's addCommands can accept List<SlashCommandData> directly
            jda.updateCommands().addCommands(commandDataList).queue();

            System.out.println("Bot started successfully!");

        } catch (Exception e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}