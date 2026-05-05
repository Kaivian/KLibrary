# Configuration

KLibrary employs a **format-agnostic** layer called `ConfigNode`. This allows the library to easily deserialize Bukkit `YamlConfiguration` or any other structured format into its core objects (`Action`, `Requirement`).

## The `ConfigNode` Concept

When defining a block in YAML, it is passed into KLibrary's deserializers as a `ConfigNode`. The node handles parsing specific values cleanly without needing a tight couple to the Spigot API.

## YAML Structure Example

Here is a common representation of how actions and requirements are configured inside a `menu/*.yml` or a `config.yml`.

```yaml
my-custom-event:
  # The Requirements list block
  requirements:
    has-vip:
      type: "permission"
      permission: "myplugin.vip"
      deny-actions:
        - "[message] <red>You must be a VIP to do this!"
        - "[sound] ENTITY_VILLAGER_NO;1.0;1.0"

  # The Actions list block
  actions:
    - "[message] <green>Welcome to the VIP zone!"
    - "[console] give %player_name% diamond 1"
    - "[sound] ENTITY_PLAYER_LEVELUP;1.0;1.0"
```

### Action Mapping

Actions are typically represented as **Lists of Strings**. 
Each string consists of a tag `[action_type]` followed by the arguments.
For example: `"[message] <gold>Hello World"` maps to the `message` Action identifier.

### Requirement Mapping

Requirements are mapped as a key-value section.
Each requirement definition has a `type` string (which matches the requirement's registered identifier), the properties of that requirement (like `permission` above), and an optional `deny-actions` list, which executes if the requirement fails.

## Menu / GUI Configurations

For inventories, KLibrary provides `InventoryConfigDeserializer` to translate typical YAML configurations directly into GUI layouts.

```yaml
my-gui:
  title: "My Awesome GUI"
  size: 27
  items:
    exit-button:
      slot: 26
      material: BARRIER
      name: "<red>Close Menu"
      actions:
        - "[close]"
```

By passing the `my-gui` section into the deserializer, KLibrary constructs the entire `InventoryProvider` and maps the defined actions onto the `exit-button` click listener automatically.
