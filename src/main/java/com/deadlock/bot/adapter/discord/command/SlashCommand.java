package com.deadlock.bot.adapter.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

/**
 * Единый интерфейс для всех слэш-команд Discord.
 */
public interface SlashCommand {

    /**
     * @return Название команды (например, "steam").
     * Правила Discord: только нижний регистр, без пробелов.
     */
    String getName();

    /**
     * @return Описание команды, которое увидят пользователи в подсказках Discord.
     */
    String getDescription();

    /**
     * @return Список параметров команды (например, текст для поиска или ID).
     */
    List<OptionData> getOptions();

    /**
     * Основной метод, который будет выполняться при вызове команды.
     * * @param event Событие JDA, содержащее информацию о пользователе, сервере и переданных аргументах.
     */
    void execute(SlashCommandInteractionEvent event);
}