package io.github.kaivian.klibrary.requirement.impl.requirements;

import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.requirement.api.RequirementResult;
import io.github.kaivian.klibrary.requirement.impl.AbstractRequirement;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Checks whether a player has a specific permission node.
 *
 * <p>Uses the tiered permission service (LuckPerms → Vault → Bukkit).</p>
 *
 * <p><b>YAML:</b></p>
 * <pre>{@code
 * type: permission
 * permission: "example.vip"
 * }</pre>
 */
public class HasPermissionRequirement extends AbstractRequirement {

    private final String permission;

    /**
     * Constructs a new {@code HasPermissionRequirement}.
     *
     * @param services   the service provider
     * @param permission the required permission node
     */
    public HasPermissionRequirement(ServiceProvider services, String permission) {
        super(services);
        this.permission = permission;
    }

    /** {@inheritDoc} */
    @Override
    public RequirementResult evaluate(ExecutionContext context) {
        return context.getPlayer().map(player ->
                services.getPermissionService().hasPermission(player, permission)
                        ? met()
                        : notMet("Missing permission: " + permission)
        ).orElse(notMet("No player in context"));
    }

    /** @return the required permission */
    public String getPermission() { return permission; }
}
