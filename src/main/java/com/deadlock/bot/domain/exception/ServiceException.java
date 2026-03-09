package com.deadlock.bot.domain.exception;

/**
 * Бизнес-исключение для ошибок сервиса.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

}