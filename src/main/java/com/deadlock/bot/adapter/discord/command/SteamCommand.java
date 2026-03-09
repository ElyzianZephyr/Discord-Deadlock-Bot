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


        playerService.getSteamProfile(query)
                .thenAccept(profileOpt -> {
                    // Этот блок выполнится ТОЛЬКО когда ответ успешно придет от API
                    if (profileOpt.isEmpty()) {
                        event.getHook().sendMessage("❌ Профиль по запросу `" + query + "` не найден.").queue();
                        return;
                    }

                    // Собираем карточку и отправляем
                    MessageEmbed embed = EmbedFactory.createProfileEmbed(profileOpt.get());
                    event.getHook().sendMessageEmbeds(embed).queue();
                })
                .exceptionally(ex -> {
                    Throwable realCause = ex.getCause() != null ? ex.getCause() : ex;
                    event.getHook().sendMessage("❌ " + realCause.getMessage()).queue();
                    return null;
                });
    }
}