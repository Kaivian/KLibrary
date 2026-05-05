# Dependency Integration

A core feature of KLibrary is handling **optional dependencies** gracefully. KLibrary avoids hard-crashing your plugin with `NoClassDefFoundError` when a third-party dependency is uninstalled by the server admin.

## Isolated Service Layer

Integrations are isolated within the `io.github.kaivian.klibrary.service` package using abstraction boundaries.

### Vault Example

When you attempt to parse a requirement that requires money, KLibrary first checks if the underlying `VaultService` is available.

```java
VaultService vault = ServiceProvider.getVaultService();

if (vault != null) {
    vault.withdraw(player, 100);
} else {
    // Vault is not installed, gracefully fallback or warn
    player.sendMessage("<red>Economy features are disabled!");
}
```

### PlaceholderAPI Example

For replacing text formatting with PlaceholderAPI variables:

```java
String rawMessage = "Hello %player_name%!";
String parsed = ServiceProvider.getPlaceholderService().setPlaceholders(player, rawMessage);
```

Behind the scenes, if PlaceholderAPI is **not** installed, the `PlaceholderService` will gracefully fallback to standard Bukkit placeholder replacements (or just return the string unmodified, relying on MiniMessage mapping).

## Developer Responsibility

When building custom actions/requirements that interact with new external plugins (e.g., ItemsAdder, Oraxen):
1. **Never** import the 3rd party API classes directly into your core `Action` implementation class.
2. Abstract the API logic into an interface (e.g., `CustomItemsService`).
3. Create an implementation of that interface (`ItemsAdderServiceImpl`).
4. In your action, retrieve the service and execute the logic safely.

This ensures KLibrary’s strict standard of fault tolerance remains intact.
