# Getting Started

This guide will walk you through setting up KLibrary in your Paper plugin project.

## Installation

You can add KLibrary as a dependency using Maven or Gradle.

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

## Plugin Setup

To use KLibrary within your Paper plugin, initialize the core registry managers in your `JavaPlugin` implementation's `onEnable` method.

```java
import io.github.kaivian.klibrary.action.api.ActionFactory;
import io.github.kaivian.klibrary.requirement.api.RequirementFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    private ActionFactory actionFactory;
    private RequirementFactory requirementFactory;

    @Override
    public void onEnable() {
        // Initialize the library factories
        this.actionFactory = new ActionFactory(this);
        this.requirementFactory = new RequirementFactory(this);

        // Register default and custom built-in actions/requirements
        // actionFactory.registerDefaults();
        // requirementFactory.registerDefaults();

        getLogger().info("MyPlugin with KLibrary enabled!");
    }
}
```

## Dependency Setup

KLibrary supports common plugins as optional dependencies. The underlying Service layer automatically handles their resolution.

Ensure your `plugin.yml` or `paper-plugin.yml` declares these dependencies if you wish to use them.

### `paper-plugin.yml`

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.myplugin.MyPlugin
api-version: '1.21'
dependencies:
  server:
    PlaceholderAPI:
      load: BEFORE
      required: false
      join-classpath: true
    Vault:
      load: BEFORE
      required: false
      join-classpath: true
```

KLibrary will silently and safely handle cases where `Vault` or `PlaceholderAPI` are absent without causing runtime exceptions.

---
_Next: [Core Concepts](03-core-concepts.md)_
