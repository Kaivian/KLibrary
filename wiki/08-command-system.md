# Command System

KLibrary provides a powerful, fluid wrapper over the Paper Command API (Mojang Brigadier mappings). This reduces the significant boilerplate associated with defining subcommands, tab completion, and permissions.

## Defining a Command

Extend `KCommand` to create a root command or a subcommand.

```java
import io.github.kaivian.klibrary.command.api.KCommand;
import org.bukkit.command.CommandSender;
import java.util.List;

public class MyRootCommand extends KCommand {

    public MyRootCommand() {
        super("myplugin", "The main command for MyPlugin");
        setPermission("myplugin.command.use");
        setAliases("mp", "myplug");

        // Register a subcommand
        addSubCommand(new ReloadSubCommand());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("MyPlugin v1.0 - Use /myplugin help for info.");
    }
}
```

## Defining Subcommands

```java
public class ReloadSubCommand extends KCommand {
    
    public ReloadSubCommand() {
        super("reload", "Reloads the plugin configuration");
        setPermission("myplugin.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Reload logic here
        sender.sendMessage("Configuration reloaded!");
    }
}
```

## Tab Completion

You can easily override the `tabComplete` method to return dynamic suggestions based on the context.

```java
@Override
public List<String> tabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
        return List.of("reload", "help", "give");
    }
    return List.of(); // Return empty list to stop suggesting player names
}
```

## Registering Commands

To register your root command with the server, use the library's `CommandManager` (instantiated via your main plugin class).

```java
// Inside your JavaPlugin onEnable
CommandManager commandManager = new CommandManager(this);
commandManager.registerCommand(new MyRootCommand());
```
