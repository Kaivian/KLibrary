# Core Concepts

KLibrary is structured around four primary pillars, designed to provide a cohesive experience for handling configuration, player interactions, conditions, and generic actions.

## 1. Action System

The **Action System** represents a series of tasks or instructions that execute upon specific triggers. Actions provide a powerful way to define behaviors like giving items, sending messages, playing sounds, or running console commands directly from configuration files. 

- Actions are parsed from configurations into functional, executable components.
- The system uses a registry structure, making it trivial to add your own custom action implementations.

[Learn more about the Action System](05-action-system.md)

## 2. Requirement System

The **Requirement System** dictates *when* something is allowed to happen. Whether a player is opening a GUI, executing an action pipeline, or claiming a reward, requirements ensure the conditions are met.

- Supports conditions like checking permissions, vault balances, or specific items in the inventory.
- Capable of logical grouping (e.g., `<Requirement A> AND (<Requirement B> OR <Requirement C>)`).

[Learn more about the Requirement System](06-requirement-system.md)

## 3. Inventory System

The **Inventory System** replaces the legacy Bukkit `InventoryHolder` implementations with a clean, stack-based GUI framework.

- `InventoryProvider`: Defines the layout, items, and title of the GUI.
- `InventoryContext`: Represents the state of the active inventory session for a player.
- **Button System**: Interactive elements abstracted cleanly to map to item clicks without repetitive `InventoryClickEvent` handling.
- **Navigation Stack**: Allows you to easily navigate forwards and backwards between different menus.

[Learn more about the Inventory System](07-inventory-system.md)

## 4. Command System

The **Command System** builds upon the modern Paper Command API (Brigadier/Mojang mappings).

- Provides simple abstractions to declare subcommands, apply permissions, handle usage formats, and configure complex tab completions with ease.

[Learn more about the Command System](08-command-system.md)
