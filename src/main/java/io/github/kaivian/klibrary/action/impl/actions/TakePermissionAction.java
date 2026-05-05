package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;

/**
 * Removes a permission node from the target player.
 *
 * <p>Uses the tiered permission service (LuckPerms → Vault → Bukkit).</p>
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: take_permission
 * permission: "example.vip"
 * }</pre>
 */
public class TakePermissionAction extends AbstractAction {

    private final String permission;

    /**
     * Constructs a new {@code TakePermissionAction}.
     *
     * @param services   the service provider
     * @param permission the permission node to remove
     */
    public TakePermissionAction(ServiceProvider services, String permission) {
        super(services);
        this.permission = permission;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            boolean success = services.getPermissionService().removePermission(player, permission);
            return success ? success() : failure("Failed to remove permission: " + permission);
        }).orElse(failure("No player in context for TakePermissionAction"));
    }

    /** @return the permission node */
    public String getPermission() { return permission; }
}
