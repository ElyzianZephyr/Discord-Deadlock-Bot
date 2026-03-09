package com.deadlock.bot.domain.exception;

public class PlayerNotFoundException extends DeadlockBotException {

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
