# Introduction

Welcome to the **KLibrary** documentation.

KLibrary is a modern, modular, and developer-friendly library designed for Paper plugins (API 1.20.6 / 1.21). It abstracts away the complex boilerplate of common systems and allows you to build robust Minecraft features with a clean, extensible architecture.

## Key Features

- **Action Engine**: A scalable, registry-based action pipeline decoupled from legacy parsing, supporting optional dependencies like Vault and PlaceholderAPI seamlessly.
- **Requirement Engine**: A powerful condition system that supports logical groupings (AND/OR) for flexible evaluations.
- **Inventory GUI Framework**: A modern, stack-based GUI framework with clean abstractions (`InventoryView`, `InventoryProvider`) and a reusable button system based on `ItemStack`.
- **Command API Wrapper**: Integrates with the modern Paper Command API (Brigadier) for subcommands, permissions, and tab completion without the boilerplate.
- **Dependency Integration**: Safe fallback systems built-in, so your plugins degrade gracefully if dependencies are missing.
- **Format-Agnostic Configuration**: Uses a `ConfigNode` layer that is detached from raw Bukkit `YamlConfiguration`, ensuring safe serialization.

## Design Philosophy

The core principle behind KLibrary is **clean architecture**.

Instead of tightly coupling specific mechanics directly into your core features, KLibrary encourages a **service-oriented approach**:

1. **Decoupling**: Services are isolated and cleanly separated from the underlying Bukkit API where possible.
2. **Modularity**: You only use what you need. Actions, requirements, and commands are loaded and managed independently.
3. **Safety First**: Optional dependencies are handled safely. The system verifies dependencies (like Vault or LuckPerms) securely at runtime to prevent `NoClassDefFoundError`.
4. **Developer Experience**: We provide fluent builders, rich abstractions, and predictable systems that make feature development fast and intuitive.

---
_Proceed to [Getting Started](02-getting-started.md) to install KLibrary in your project._
