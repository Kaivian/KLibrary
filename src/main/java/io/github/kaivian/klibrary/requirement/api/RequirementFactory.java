package io.github.kaivian.klibrary.requirement.api;

import io.github.kaivian.klibrary.config.ConfigNode;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Factory for creating {@link Requirement} instances from configuration data.
 *
 * <p>Each registered requirement type has an associated factory that knows
 * how to parse its specific configuration fields and construct the
 * corresponding requirement instance.</p>
 *
 * <p><b>Registration example:</b></p>
 * <pre>{@code
 * registry.register("permission", (config, services) -> {
 *     String perm = config.getString("permission", "");
 *     return new HasPermissionRequirement(services.getPermissionService(), perm);
 * });
 * }</pre>
 *
 * @see RequirementRegistry
 * @see Requirement
 */
@FunctionalInterface
public interface RequirementFactory {

    /**
     * Creates a {@link Requirement} from the given configuration and services.
     *
     * @param config   the configuration node containing requirement parameters
     * @param services the service provider for dependency injection
     * @return the constructed requirement; must not be {@code null}
     * @throws IllegalArgumentException if required configuration fields are missing
     */
    Requirement create(ConfigNode config, ServiceProvider services);
}
