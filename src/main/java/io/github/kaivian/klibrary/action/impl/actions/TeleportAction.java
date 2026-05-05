package io.github.kaivian.klibrary.action.impl.actions;

import io.github.kaivian.klibrary.action.api.ActionResult;
import io.github.kaivian.klibrary.action.impl.AbstractAction;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.service.ServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Teleports the target player to specified coordinates.
 *
 * <p><b>YAML configuration:</b></p>
 * <pre>{@code
 * type: teleport
 * world: "world"
 * x: 100.5
 * y: 64.0
 * z: -200.5
 * yaw: 0.0        # optional, defaults to player's current yaw
 * pitch: 0.0      # optional, defaults to player's current pitch
 * }</pre>
 */
public class TeleportAction extends AbstractAction {

    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final Float yaw;
    private final Float pitch;

    /**
     * Constructs a new {@code TeleportAction}.
     *
     * @param services  the service provider
     * @param worldName the target world name (may be {@code null} to use player's current world)
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     * @param yaw       the yaw rotation (may be {@code null} to keep current)
     * @param pitch     the pitch rotation (may be {@code null} to keep current)
     */
    public TeleportAction(ServiceProvider services, String worldName,
                          double x, double y, double z, Float yaw, Float pitch) {
        super(services);
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResult execute(ExecutionContext context) {
        return requirePlayer(context).map(player -> {
            World world = worldName != null ? Bukkit.getWorld(worldName) : player.getWorld();
            if (world == null) {
                return failure("World not found: " + worldName);
            }

            float finalYaw = yaw != null ? yaw : player.getLocation().getYaw();
            float finalPitch = pitch != null ? pitch : player.getLocation().getPitch();

            Location location = new Location(world, x, y, z, finalYaw, finalPitch);
            player.teleport(location);
            return success();
        }).orElse(failure("No player in context for TeleportAction"));
    }

    /** @return the target world name */
    public String getWorldName() { return worldName; }

    /** @return the x coordinate */
    public double getX() { return x; }

    /** @return the y coordinate */
    public double getY() { return y; }

    /** @return the z coordinate */
    public double getZ() { return z; }
}
