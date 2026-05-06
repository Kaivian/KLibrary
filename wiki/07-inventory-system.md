# Inventory System Documentation

The KLibrary Inventory System replaces traditional, event-heavy Bukkit inventory handling with a modern, modular, and state-driven GUI framework designed exclusively for Paper servers. 

To ensure complete clarity and a professional developer experience, the documentation for this system is split into multiple focused topics. Please read them in order to understand the full capabilities of the API.

## Table of Contents

### 1. [Getting Started: Creating and Opening a GUI](./07-1-inventory-getting-started)
Learn the core architectural rules (Context vs Provider vs View) and how to construct, register, and display your first GUI to a player.

### 2. [Default Buttons Setup System](./07-2-inventory-default-buttons)
Understand the prioritized rendering hierarchy (View vs Provider levels) and how to safely inject built-in UI controls like `BackButton` and `CloseButton`.

### 3. [Pagination System (Next / Previous Page GUI)](./07-3-inventory-pagination)
Discover how to build scrolling item lists without recreating inventories using KLibrary's state-driven context engine and built-in `NavigationButton` implementations.

### 4. [Updating GUI Dynamically (Live Refresh System)](./07-4-inventory-dynamic-updates)
Learn the distinct differences between a state update (`update()`) and a view transition (`openReplace()`), and how to hook into the global auto-refresh ticker.

### 5. [GUI Click Handling System](./07-5-inventory-click-handling)
Explore the execution flow of a player interaction. Learn how the framework intercepts, secures, and routes clicks directly to stateless `Button` dispatchers.

### 6. [GUI Animation System (Transitions & Effects)](./07-6-inventory-animation)
Find out how to simulate smooth visual transitions—like slot-by-slot reveals and loading screens—using timed update cycles rather than disrupting the client UI.

### 7. [Advanced GUI Update Patterns](./07-7-inventory-advanced-patterns)
Master optimization strategies. Learn how to perform targeted slot updates, avoid full rebuilds, and throttle events to keep your menus highly performant on large servers.
