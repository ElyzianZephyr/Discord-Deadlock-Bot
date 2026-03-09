package com.deadlock.bot.adapter.discord.command;

import com.deadlock.bot.adapter.discord.view.EmbedFactory;
import com.deadlock.bot.domain.model.HeroStats;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.deadlock.bot.service.PlayerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * Слэш-команда /stats для получения статистики игрока по героям.
 */
public class StatsCommand implements SlashCommand {

    private final PlayerService playerService;

    // Внедряем сервис через конструктор
    public StatsCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public String getName() {
        return "stats"; // Имя команды в Discord
    }

    @Override
    public String getDescription() {
        return "Получить статистику игрока по героям (требуется account_id)";
    }

    @Override
    public List<OptionData> getOptions() {
        // Добавляем обязательный параметр "account_id"
        return List.of(
                new OptionData(OptionType.STRING, "account_id", "Уникальный ID аккаунта игрока (цифры)", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping accountIdOption = event.getOption("account_id");

        if (accountIdOption == null) {
            event.getHook().sendMessage("Необходимо указать account_id.").queue();
            return;
        }

        String accountIdStr = accountIdOption.getAsString();

        playerService.getPlayerStats(accountIdStr)
                .thenAccept(stats -> {
                    if (stats.isEmpty()) {
                        event.getHook().sendMessage("❌ Статистика для игрока не найдена (возможно, скрыт профиль).").queue();
                        return;
                    }

                    int accountId = Integer.parseInt(accountIdStr.trim());

                    MessageEmbed embed = EmbedFactory.createStatsEmbed(accountId, stats);
                    event.getHook().sendMessageEmbeds(embed).queue();
                })
                .exceptionally(ex -> {
                    Throwable realCause = ex.getCause() != null ? ex.getCause() : ex;
                    event.getHook().sendMessage("❌ Произошла ошибка при получении статистики: " + ex.getMessage()).queue();
                    return null;
                });
    }
}