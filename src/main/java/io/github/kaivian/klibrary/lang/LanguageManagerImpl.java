package io.github.kaivian.klibrary.lang;

import io.github.kaivian.klibrary.KLibrary;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LanguageManagerImpl implements LanguageManager {

    private final KLibrary plugin;
    private final Map<Locale, YamlConfiguration> loadedLocales = new ConcurrentHashMap<>();
    private final Locale DEFAULT_LOCALE = Locale.US; // en_US

    public LanguageManagerImpl(KLibrary plugin) {
        this.plugin = plugin;
        saveDefaultLanguages();
        reload();
    }

    private void saveDefaultLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        String[] defaults = {"en_US.yml", "vi_VN.yml"};
        for (String file : defaults) {
            File langFile = new File(langFolder, file);
            if (!langFile.exists()) {
                if (plugin.getResource("lang/" + file) != null) {
                    plugin.saveResource("lang/" + file, false);
                } else {
                    plugin.getLogger().warning("Default lang file 'lang/" + file + "' not found in jar!");
                }
            }
        }
    }

    @Override
    public void reload() {
        loadedLocales.clear();
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            return;
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String localeName = file.getName().replace(".yml", "");
            try {
                String[] parts = localeName.split("_");
                Locale locale;
                if (parts.length == 2) {
                    locale = new Locale(parts[0], parts[1]);
                } else {
                    locale = new Locale(parts[0]);
                }
                
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                loadedLocales.put(locale, config);
                plugin.getLogger().info("Loaded language file: " + file.getName());
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load language file: " + file.getName(), e);
            }
        }
    }

    @Override
    public @NotNull Component get(@NotNull Player player, @NotNull String key, @NotNull TagResolver... resolvers) {
        return get(player.locale(), key, resolvers);
    }

    @Override
    public @NotNull Component get(@NotNull Locale locale, @NotNull String key, @NotNull TagResolver... resolvers) {
        String raw = getRaw(locale, key);
        return MiniMessage.miniMessage().deserialize(raw, resolvers);
    }

    @Override
    public @NotNull Component getGlobal(@NotNull String key, @NotNull TagResolver... resolvers) {
        return get(DEFAULT_LOCALE, key, resolvers);
    }

    @Override
    public @NotNull List<Component> getList(@NotNull Locale locale, @NotNull String key, @NotNull TagResolver... resolvers) {
        YamlConfiguration config = getConfigFallback(locale);
        List<String> rawList = null;
        
        if (config != null) {
            rawList = config.getStringList(key);
        }
        
        if (rawList == null || rawList.isEmpty()) {
            config = getConfigFallback(DEFAULT_LOCALE);
            if (config != null) {
                rawList = config.getStringList(key);
            }
        }
        
        if (rawList == null) {
            return Collections.singletonList(Component.text(key));
        }

        MiniMessage mm = MiniMessage.miniMessage();
        List<Component> components = new ArrayList<>();
        for (String raw : rawList) {
            components.add(mm.deserialize(raw, resolvers));
        }
        return components;
    }

    @Override
    public @NotNull String getRaw(@NotNull Locale locale, @NotNull String key) {
        YamlConfiguration config = getConfigFallback(locale);
        String value = null;
        
        if (config != null) {
            value = config.getString(key);
        }
        
        if (value == null) {
            config = getConfigFallback(DEFAULT_LOCALE);
            if (config != null) {
                value = config.getString(key);
            }
        }
        
        return value != null ? value : key;
    }

    private YamlConfiguration getConfigFallback(Locale locale) {
        // 1. Exact match (e.g. en_US)
        YamlConfiguration exact = loadedLocales.get(locale);
        if (exact != null) return exact;

        // 2. Language only match (e.g. en)
        Locale langOnly = new Locale(locale.getLanguage());
        YamlConfiguration langExact = loadedLocales.get(langOnly);
        if (langExact != null) return langExact;

        // 3. Try to find any locale matching the language (e.g. en_UK instead of en_US)
        for (Map.Entry<Locale, YamlConfiguration> entry : loadedLocales.entrySet()) {
            if (entry.getKey().getLanguage().equals(locale.getLanguage())) {
                return entry.getValue();
            }
        }

        return null;
    }
}
