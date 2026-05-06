package io.github.kaivian.klibrary.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * Manages plugin localization and translations using Adventure API and MiniMessage.
 *
 * <p>Supports fallback chains: Player Locale -> Language only -> en_US.</p>
 */
public interface LanguageManager {

    /**
     * Gets a localized component for a specific player.
     *
     * @param player    the player to get the locale from
     * @param key       the translation key
     * @param resolvers optional MiniMessage tag resolvers
     * @return the localized component
     */
    @NotNull Component get(@NotNull Player player, @NotNull String key, @NotNull TagResolver... resolvers);

    /**
     * Gets a localized component for a specific locale.
     *
     * @param locale    the locale
     * @param key       the translation key
     * @param resolvers optional MiniMessage tag resolvers
     * @return the localized component
     */
    @NotNull Component get(@NotNull Locale locale, @NotNull String key, @NotNull TagResolver... resolvers);

    /**
     * Gets a localized component using the default global locale (en_US).
     *
     * @param key       the translation key
     * @param resolvers optional MiniMessage tag resolvers
     * @return the localized component
     */
    @NotNull Component getGlobal(@NotNull String key, @NotNull TagResolver... resolvers);

    /**
     * Gets a list of localized components for a specific locale.
     *
     * @param locale    the locale
     * @param key       the translation key
     * @param resolvers optional MiniMessage tag resolvers
     * @return the list of localized components
     */
    @NotNull List<Component> getList(@NotNull Locale locale, @NotNull String key, @NotNull TagResolver... resolvers);

    /**
     * Gets the raw string translation for a locale without parsing it to a Component.
     *
     * @param locale the locale
     * @param key    the translation key
     * @return the raw string
     */
    @NotNull String getRaw(@NotNull Locale locale, @NotNull String key);

    /**
     * Reloads all language files from disk.
     */
    void reload();
}
