package io.github.kaivian.klibrary.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.kaivian.klibrary.KLibrary;
import io.github.kaivian.klibrary.context.ExecutionContext;
import io.github.kaivian.klibrary.inventory.api.InventoryContext;
import io.github.kaivian.klibrary.inventory.api.InventoryManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KLibraryCommand {

    private final KLibrary plugin;

    public KLibraryCommand(KLibrary plugin) {
        this.plugin = plugin;
    }

    public void register(Commands commands) {
        commands.register(
            Commands.literal("klibrary")
                .requires(source -> source.getSender().hasPermission("klibrary.admin"))
                .then(Commands.literal("reload")
                    .executes(this::executeReload))
                .then(Commands.literal("open")
                    .then(Commands.argument("menu", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            plugin.getServiceProvider().getInventoryManager().ifPresent(manager -> {
                                // Normally we would get all registered provider IDs.
                                // But since we don't expose it yet, we just return empty or maybe we can fetch them.
                            });
                            return builder.buildFuture();
                        })
                        .executes(context -> executeOpen(context, null))
                        .then(Commands.argument("player", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                        builder.suggest(player.getName());
                                    }
                                }
                                return builder.buildFuture();
                            })
                            .executes(context -> executeOpen(context, StringArgumentType.getString(context, "player")))
                        )
                    )
                )
                .then(Commands.literal("debug")
                    .executes(this::executeDebug))
                .build(),
            "KLibrary main command",
            java.util.List.of("klib")
        );
    }

    private int executeReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.getSender().sendMessage(Component.text("Reloading KLibrary...").color(NamedTextColor.YELLOW));
        
        // Reload configurations, registries, and menus
        plugin.reload();
        
        source.getSender().sendMessage(Component.text("KLibrary reloaded successfully!").color(NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private int executeOpen(CommandContext<CommandSourceStack> context, String targetPlayerName) {
        CommandSourceStack source = context.getSource();
        String menuId = StringArgumentType.getString(context, "menu");

        Player target = null;
        if (targetPlayerName != null) {
            target = Bukkit.getPlayer(targetPlayerName);
        } else if (source.getExecutor() instanceof Player player) {
            target = player;
        }

        if (target == null) {
            source.getSender().sendMessage(Component.text("Target player not found or must be executed by a player.").color(NamedTextColor.RED));
            return 0;
        }

        Player finalTarget = target;
        plugin.getServiceProvider().getInventoryManager().ifPresentOrElse(manager -> {
            if (manager.getProvider(menuId).isPresent()) {
                ExecutionContext execCtx = ExecutionContext.builder()
                        .player(finalTarget)
                        .sender(source.getSender())
                        .plugin(plugin)
                        .build();
                manager.push(finalTarget, menuId, InventoryContext.from(execCtx).build());
                source.getSender().sendMessage(Component.text("Opened menu " + menuId + " for " + finalTarget.getName()).color(NamedTextColor.GREEN));
            } else {
                source.getSender().sendMessage(Component.text("Menu " + menuId + " not found.").color(NamedTextColor.RED));
            }
        }, () -> {
            source.getSender().sendMessage(Component.text("Inventory Manager is not available.").color(NamedTextColor.RED));
        });

        return Command.SINGLE_SUCCESS;
    }

    private int executeDebug(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        // Toggle debug logic here
        source.getSender().sendMessage(Component.text("Debug mode toggled.").color(NamedTextColor.AQUA));
        return Command.SINGLE_SUCCESS;
    }
}
