package io.github.kaivian.klibrary.service;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Service for parsing and sending rich messages using Paper's Adventure API.
 *
 * <p>This service integrates MiniMessage formatting with optional PlaceholderAPI
 * resolution. All text output in the action/requirement engine should go through
 * this service to ensure consistent formatting and placeholder handling.</p>
 *
 * <p><b>Resolution order:</b></p>
 * <ol>
 *   <li>Custom placeholders from {@code ExecutionContext} ({@code {key}} format)</li>
 *   <li>PlaceholderAPI placeholders ({@code %placeholder%} format) — if available</li>
 *   <li>MiniMessage tag parsing ({@code <tag>} format)</li>
 * </ol>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * MessageService messageService = new MessageService(dependencyService);
 * messageService.send(player, "<green>Hello {name}! Balance: %vault_eco_balance%",
 *     Map.of("name", "Steve"));
 * }</pre>
 */
public class MessageService {

    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final PluginDependencyService dependencyService;

    /**
     * Constructs a new {@code MessageService}.
     *
     * @param dependencyService the plugin dependency service for checking PlaceholderAPI
     */
    public MessageService(PluginDependencyService dependencyService) {
        this.dependencyService = dependencyService;
    }

    /**
     * Parses a MiniMessage string into a {@link Component}, resolving all
     * placeholders.
     *
     * @param message           the raw MiniMessage string
     * @param player            the player context for PlaceholderAPI (may be {@code null})
     * @param customPlaceholders custom key-value placeholders to apply
     * @return the parsed {@link Component}
     */
    public Component parse(String message, Player player, Map<String, String> customPlaceholders) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        String resolved = message;

        // Step 1: Apply custom placeholders ({key} format)
        if (customPlaceholders != null && !customPlaceholders.isEmpty()) {
            for (Map.Entry<String, String> entry : customPlaceholders.entrySet()) {
                resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        // Step 2: Apply PlaceholderAPI (%placeholder% format) if available
        if (player != null && dependencyService.hasPlaceholderAPI()) {
            try {
                resolved = PlaceholderAPI.setPlaceholders(player, resolved);
            } catch (Exception e) {
                LOGGER.warning("PlaceholderAPI resolution failed: " + e.getMessage());
            }
        }

        // Step 3: Parse MiniMessage
        return MINI_MESSAGE.deserialize(resolved);
    }

    /**
     * Parses a MiniMessage string into a {@link Component} without custom placeholders.
     *
     * @param message the raw MiniMessage string
     * @param player  the player context for PlaceholderAPI (may be {@code null})
     * @return the parsed {@link Component}
     */
    public Component parse(String message, Player player) {
        return parse(message, player, Map.of());
    }

    /**
     * Sends a rich message to a player.
     *
     * <p>The message is parsed through the full resolution pipeline
     * (custom placeholders → PlaceholderAPI → MiniMessage) and then
     * sent to the player via Adventure's {@code sendMessage()}.</p>
     *
     * @param player            the recipient
     * @param message           the raw MiniMessage string
     * @param customPlaceholders custom key-value placeholders
     */
    public void send(Player player, String message, Map<String, String> customPlaceholders) {
        if (player == null || message == null || message.isEmpty()) {
            return;
        }
        player.sendMessage(parse(message, player, customPlaceholders));
    }

    /**
     * Sends a rich message to a player without custom placeholders.
     *
     * @param player  the recipient
     * @param message the raw MiniMessage string
     */
    public void send(Player player, String message) {
        send(player, message, Map.of());
    }

    /**
     * Sends a title and subtitle to a player using Adventure API.
     *
     * @param player   the recipient
     * @param title    the title MiniMessage string (may be empty)
     * @param subtitle the subtitle MiniMessage string (may be empty)
     * @param fadeIn   fade-in duration in ticks
     * @param stay     stay duration in ticks
     * @param fadeOut  fade-out duration in ticks
     * @param customPlaceholders custom placeholders
     */
    public void sendTitle(Player player, String title, String subtitle,
                          int fadeIn, int stay, int fadeOut,
                          Map<String, String> customPlaceholders) {
        if (player == null) return;

        Component titleComponent = parse(title != null ? title : "", player, customPlaceholders);
        Component subtitleComponent = parse(subtitle != null ? subtitle : "", player, customPlaceholders);

        player.showTitle(net.kyori.adventure.title.Title.title(
                titleComponent,
                subtitleComponent,
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(fadeIn * 50L),
                        java.time.Duration.ofMillis(stay * 50L),
                        java.time.Duration.ofMillis(fadeOut * 50L)
                )
        ));
    }

    /**
     * Sends an action bar message to a player.
     *
     * @param player            the recipient
     * @param message           the MiniMessage string
     * @param customPlaceholders custom placeholders
     */
    public void sendActionBar(Player player, String message, Map<String, String> customPlaceholders) {
        if (player == null || message == null || message.isEmpty()) return;
        player.sendActionBar(parse(message, player, customPlaceholders));
    }
}
