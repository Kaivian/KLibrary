package io.github.kaivian.klibrary.action.api;

import io.github.kaivian.klibrary.config.ConfigNode;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Factory for creating {@link Action} instances from configuration data.
 *
 * <p>Each registered action type has an associated factory that knows how to
 * parse its specific configuration fields and construct the corresponding
 * action instance. Factories receive the {@link ServiceProvider} to inject
 * service dependencies into the created actions.</p>
 *
 * <p><b>Registration example:</b></p>
 * <pre>{@code
 * registry.register("message", (config, services) -> {
 *     String message = config.getString("message", "");
 *     return new MessageAction(services.getMessageService(), message);
 * });
 * }</pre>
 *
 * @see ActionRegistry
 * @see Action
 */
@FunctionalInterface
public interface ActionFactory {

    /**
     * Creates an {@link Action} from the given configuration and services.
     *
     * @param config   the configuration node containing action parameters
     * @param services the service provider for dependency injection
     * @return the constructed action; must not be {@code null}
     * @throws IllegalArgumentException if required configuration fields are missing
     */
    Action create(ConfigNode config, ServiceProvider services);
}
