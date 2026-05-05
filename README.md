# KLibrary

A powerful, modern, and modular library for building Minecraft Paper plugins. KLibrary abstracts away the complex boilerplate of actions, conditions, commands, and inventory GUIs, allowing developers to focus on writing clean, scalable feature logic.

## Key Features

- **Action Engine**: Define dynamic behaviors (messages, sounds, commands) in configuration files and parse them effortlessly.
- **Requirement Engine**: Flexible condition checking (permissions, money, items) with built-in support for complex `AND`/`OR` logical groupings.
- **Inventory GUI Framework**: A completely stack-based, modular GUI system utilizing `InventoryProvider`, `InventoryContext`, and interactive `Button` abstractions.
- **Command API**: Fluent abstractions built on top of the modern Paper Command API (Brigadier) for subcommands, permissions, and dynamic tab completions.
- **Dependency Integration**: Built-in, fault-tolerant service layers that handle optional dependencies like Vault and PlaceholderAPI seamlessly.

## Supported Platforms

- **Paper API:** 1.20.6 / 1.21
- **Java Version:** Java 21+

## Installation

Add KLibrary to your project via Maven or Gradle.

### Maven

```xml
<dependency>
    <groupId>io.github.kaivian</groupId>
    <artifactId>klibrary</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
    implementation 'io.github.kaivian:klibrary:1.0.0-SNAPSHOT'
}
```

## Quick Start Example

KLibrary allows you to bind configuration directly to logic.

**`config.yml`:**
```yaml
reward-click:
  requirements:
    has-perm:
      type: "permission"
      permission: "myplugin.vip"
      deny-actions:
        - "[message] <red>You need VIP to do this!"
  actions:
    - "[message] <green>You claimed your VIP reward!"
    - "[console] give %player_name% diamond 1"
```

**`JavaPlugin` class:**
```java
// Initialize the core library managers
ActionFactory actionFactory = new ActionFactory(this);
RequirementFactory requirementFactory = new RequirementFactory(this);

// Parse logic from a config node
ConfigNode node = ConfigNode.from(getConfig().getConfigurationSection("reward-click"));
List<Requirement> reqs = requirementFactory.deserializeRequirements(node.getList("requirements"));
List<Action> actions = actionFactory.deserializeActions(node.getList("actions"));

// Evaluate requirements and execute actions
if (RequirementResult.evaluateAll(player, reqs).isSuccess()) {
    actions.forEach(action -> action.execute(player));
}
```

## Documentation

Full documentation is available in the [`/wiki`](./wiki) folder.

1. [Introduction](./wiki/01-introduction.md)
2. [Getting Started](./wiki/02-getting-started.md)
3. [Core Concepts](./wiki/03-core-concepts.md)
4. [Configuration](./wiki/04-configuration.md)
5. [Action System](./wiki/05-action-system.md)
6. [Requirement System](./wiki/06-requirement-system.md)
7. [Inventory System](./wiki/07-inventory-system.md)
8. [Command System](./wiki/08-command-system.md)
9. [Dependency Integration](./wiki/09-dependency-integration.md)
10. [Advanced Usage](./wiki/10-advanced-usage.md)

---
*Built with clean architecture in mind to make Minecraft development enjoyable.*
