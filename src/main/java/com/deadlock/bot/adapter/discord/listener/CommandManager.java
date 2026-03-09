package com.deadlock.bot.adapter.discord.listener;

import com.deadlock.bot.adapter.discord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Маршрутизатор слэш-команд. Слушает события Discord и вызывает нужную команду.
 */
public class CommandManager extends ListenerAdapter {

    private final Map<String, SlashCommand> commands = new HashMap<>();

    // В конструктор передаем список всех наших команд
    public CommandManager(List<SlashCommand> commandList) {
        for (SlashCommand command : commandList) {
            commands.put(command.getName(), command);
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = commands.get(event.getName());
        if (command == null) {
            event.reply("Неизвестная команда!").setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        command.execute(event);
    }

    /**
     * Возвращает список всех зарегистрированных команд.
     * Понадобится нам при запуске бота для отправки структуры команд на сервер Discord.
     */
    public List<SlashCommand> getRegisteredCommands() {
        return commands.values().stream().toList();
    }
}