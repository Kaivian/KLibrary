package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Sends a title and subtitle to the target player using Adventure API.
 *
 * <p>Both title and subtitle support MiniMessage formatting and PlaceholderAPI.</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: title
 * title: "<gold><bold>WELCOME"
 * subtitle: "<gray>Enjoy your stay, {player_name}!"
 * fade_in: 10     # ticks (default: 10)
 * stay: 70        # ticks (default: 70)
 * fade_out: 20    # ticks (default: 20)
 * }</pre>
 */
public class TitleAction extends AbstractAction {

    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    /**
     * Constructs a new {@code TitleAction}.
     *
     * @param services the service provider
     * @param title    the title text (MiniMessage format)
     * @param subtitle the subtitle text (MiniMessage format)
     * @param fadeIn   fade-in duration in ticks
     * @param stay     stay duration in ticks
     * @param fadeOut  fade-out duration in ticks
     */
    public TitleAction(ServiceProvider services, String title, String subtitle,
                       int fadeIn, int stay, int fadeOut) {
        super(services);
        this.title = title != null ? title : "";
        this.subtitle = subtitle != null ? subtitle : "";
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            services.getMessageService().sendTitle(player, title, subtitle,
                    fadeIn, stay, fadeOut, context.getPlaceholders());
            return success();
        }).orElse(failure("No player in context for TitleAction"));
    }

    /** @return the title text */
    public String getTitle() { return title; }

    /** @return the subtitle text */
    public String getSubtitle() { return subtitle; }

    /** @return the fade-in duration in ticks */
    public int getFadeIn() { return fadeIn; }

    /** @return the stay duration in ticks */
    public int getStay() { return stay; }

    /** @return the fade-out duration in ticks */
    public int getFadeOut() { return fadeOut; }
}
