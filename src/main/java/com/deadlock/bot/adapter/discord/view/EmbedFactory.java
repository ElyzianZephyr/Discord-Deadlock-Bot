package com.deadlock.bot.adapter.discord.view;

import com.deadlock.bot.domain.model.HeroStats;
import com.deadlock.bot.domain.model.SteamProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import com.deadlock.bot.domain.model.MatchHistory;
import com.deadlock.bot.domain.model.Hero;

import java.awt.Color;
import java.util.List;

/**
 * Фабрика для генерации красивых карточек (Embed) для Discord.
 */
public class EmbedFactory {

    // Фирменный цвет для карточек (можно поменять на любой HEX-код)
    private static final Color DEADLOCK_COLOR = Color.decode("#d35400");
    private static final Color MATCH_COLOR = Color.decode("#2ecc71"); // Изумрудно-зеленый

    /**
     * Создает карточку профиля Steam.
     */
    public static MessageEmbed createProfileEmbed(SteamProfile profile) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(DEADLOCK_COLOR);
        builder.setTitle("🎮 Профиль игрока: " + profile.getPersonaName(), profile.getSteamProfileLink());

        // Устанавливаем аватарку игрока как миниатюру справа
        if (profile.getAvatarFull() != null && !profile.getAvatarFull().isEmpty()) {
            builder.setThumbnail(profile.getAvatarFull());
        }

        // Добавляем поля (inline = true означает, что они будут в одну строку)
        builder.addField("Account ID", String.valueOf(profile.getAccountId()), true);
        builder.addField("Steam ID", String.valueOf(profile.getSteamId64()), true);

        // Скрытая ссылка в тексте (формат Markdown)
        builder.addField("Steam", "[Открыть профиль](" + profile.getSteamProfileLink() + ")", false);

        builder.setFooter("Deadlock Stats Bot", null);

        return builder.build();
    }

    /**
     * Создает карточку со статистикой по героям.
     */
    public static MessageEmbed createStatsEmbed(int accountId, List<HeroStats> stats) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(DEADLOCK_COLOR);
        builder.setTitle("📊 Статистика героев (Account ID: " + accountId + ")");

        // Берем топ-10 героев для вывода
        List<HeroStats> topHeroes = stats.stream().limit(10).toList();

        for (HeroStats hero : topHeroes) {
            // Вычисляем нужные метрики
            double winRate = hero.getMatchesPlayed() > 0 ? (hero.getWins() * 100.0 / hero.getMatchesPlayed()) : 0;
            double kda = hero.getDeaths() > 0 ? (double)(hero.getKills() + hero.getAssists()) / hero.getDeaths() : (hero.getKills() + hero.getAssists());
            int minutesPlayed = hero.getTimePlayed() / 60;

            // Название поля (пока используем ID героя, на Этапе 5 заменим на имя)
            String fieldTitle = "🦸‍♂️ " + Hero.getNameById(hero.getHeroId());

            // Значение поля с использованием эмодзи и форматирования Markdown
            String fieldValue = String.format(
                    "⚔️ **Матчей:** %d (Побед: %.1f%%)\n🎯 **K/D/A:** %d/%d/%d (*%.2f*)\n⏱️ **Время:** %d мин",
                    hero.getMatchesPlayed(), winRate,
                    hero.getKills(), hero.getDeaths(), hero.getAssists(), kda,
                    minutesPlayed
            );

            // Добавляем поле в карточку (false = каждое поле с новой строки)
            builder.addField(fieldTitle, fieldValue, false);
        }

        // Если героев больше 10, пишем об этом в подвале карточки
        if (stats.size() > 10) {
            builder.setFooter("Показаны топ-10 героев. Всего сыграно на " + stats.size() + " | Deadlock Stats Bot", null);
        } else {
            builder.setFooter("Deadlock Stats Bot", null);
        }

        return builder.build();
    }

    /**
     * Создает карточку с историей последних матчей.
     */
    public static MessageEmbed createMatchesEmbed(int accountId, List<MatchHistory> matchHistory) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(MATCH_COLOR);
        builder.setTitle("📜 История матчей (Account ID: " + accountId + ")");

        // Берем последние 10 матчей
        List<MatchHistory> recentMatches = matchHistory.stream().limit(10).toList();

        for (MatchHistory match : recentMatches) {
            // Визуальное выделение победы и поражения
            String resultText = match.isWin() ? "🟢 **Победа**" : "🔴 **Поражение**";

            // Заголовок блока (Результат и ID матча)
            String fieldTitle = resultText + " (Матч: " + match.getMatchId() + ")";

            // Текст блока (Герой, KDA, Время)
            String fieldValue = String.format(
                    "🦸‍♂️ **Герой:** %s\n🎯 **K/D/A:** %d/%d/%d (*%.2f*)\n⏱️ **Время:** %s",
                    Hero.getNameById(match.getHeroId()),
                    match.getKills(), match.getDeaths(), match.getAssists(), match.getKda(),
                    match.getFormattedDuration()
            );

            // Добавляем блок в карточку
            builder.addField(fieldTitle, fieldValue, false);
        }

        if (matchHistory.size() > 10) {
            builder.setFooter("Показаны последние 10 матчей. Всего найдено: " + matchHistory.size() + " | Deadlock Stats Bot", null);
        } else {
            builder.setFooter("Deadlock Stats Bot", null);
        }

        return builder.build();
    }
}