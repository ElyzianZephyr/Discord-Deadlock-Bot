package com.deadlock.bot.adapter.discord.command;

import com.deadlock.bot.adapter.discord.view.EmbedFactory;
import com.deadlock.bot.domain.model.MatchHistory;
import com.deadlock.bot.service.PlayerService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * Слэш-команда /matches для получения истории последних матчей игрока.
 */
public class MatchesCommand implements SlashCommand {

    private final PlayerService playerService;

    public MatchesCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public String getName() {
        return "matches"; // Имя команды в Discord
    }

    @Override
    public String getDescription() {
        return "Получить историю последних матчей игрока (требуется account_id)";
    }

    @Override
    public List<OptionData> getOptions() {
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

        try {
            // 1. Получаем историю матчей из сервиса
            List<MatchHistory> matchHistory = playerService.getMatchHistory(accountIdStr);

            if (matchHistory.isEmpty()) {
                event.getHook().sendMessage("❌ История матчей для данного игрока не найдена.").queue();
                return;
            }

            int accountId = Integer.parseInt(accountIdStr.trim());

            // 2. Создаем карточку (этот метод мы добавим в EmbedFactory на следующем шаге!)
            MessageEmbed embed = EmbedFactory.createMatchesEmbed(accountId, matchHistory);

            // 3. Отправляем карточку в Discord
            event.getHook().sendMessageEmbeds(embed).queue();

        } catch (IllegalArgumentException e) {
            event.getHook().sendMessage("⚠️ " + e.getMessage()).queue();
        } catch (Exception e) {
            event.getHook().sendMessage("❌ Произошла ошибка при получении истории матчей: " + e.getMessage()).queue();
        }
    }
}