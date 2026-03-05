package com.deadlock.bot.adapter.discord.command;

import com.deadlock.bot.adapter.discord.view.EmbedFactory;
import com.deadlock.bot.domain.model.SteamProfile;
import com.deadlock.bot.service.PlayerService;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Optional;

/**
 * Слэш-команда /steam для поиска профиля игрока с выводом через Embed.
 */
public class SteamCommand implements SlashCommand {

    private final PlayerService playerService;

    public SteamCommand(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public String getName() {
        return "steam";
    }

    @Override
    public String getDescription() {
        return "Поиск профиля игрока в Deadlock по никнейму или Steam ID";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "query", "Никнейм или Steam ID для поиска", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping queryOption = event.getOption("query");

        if (queryOption == null) {
            event.getHook().sendMessage("Необходимо указать параметр поиска.").queue();
            return;
        }

        String query = queryOption.getAsString();

        try {
            // Обращаемся к сервису за объектом профиля (возвращает Optional)
            Optional<SteamProfile> profileOpt = playerService.getSteamProfile(query);

            if (profileOpt.isEmpty()) {
                // Если Optional пустой — профиль не найден
                event.getHook().sendMessage("❌ Профиль по запросу `" + query + "` не найден.").queue();
                return;
            }

            // Передаем найденный объект профиля в нашу фабрику для отрисовки
            MessageEmbed embed = EmbedFactory.createProfileEmbed(profileOpt.get());

            // Отправляем готовую карточку (Embed) в Discord
            event.getHook().sendMessageEmbeds(embed).queue();

        } catch (IllegalArgumentException e) {
            // Перехватываем ошибку пустого ввода из PlayerService
            event.getHook().sendMessage("⚠️ " + e.getMessage()).queue();
        } catch (Exception e) {
            // Перехватываем любые другие ошибки (например, упал сервер API)
            event.getHook().sendMessage("❌ Произошла ошибка при поиске профиля: " + e.getMessage()).queue();
        }
    }
}