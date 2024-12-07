package org.alexdev.itemcomponents.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import org.alexdev.itemcomponents.ItemComponents;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Getter
public class ConfigManager {

    private static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .charset(StandardCharsets.UTF_8)
            .outputNulls(true)
            .inputNulls(false)
            .footer("Authors: AlexDev_")
            .build();

    private final ItemComponents plugin;
    private Settings settings;

    public ConfigManager(@NotNull ItemComponents plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public Optional<Throwable> loadConfigs() {
        final File settingsFile = new File(plugin.getDataFolder(), "settings.yml");

        try {
            settings = YamlConfigurations.update(
                    settingsFile.toPath(),
                    Settings.class,
                    PROPERTIES
            );
            checkData();
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e);
        }
    }

    public void reload() {
        settings = YamlConfigurations.load(new File(plugin.getDataFolder(), "settings.yml").toPath(), Settings.class, PROPERTIES);
        checkData();
    }

    private void checkData() {
        if (settings == null) {
            throw new IllegalStateException("Settings not loaded");
        }



    }

    public void save() {
        YamlConfigurations.save(new File(plugin.getDataFolder(), "settings.yml").toPath(), Settings.class, settings, PROPERTIES);
    }
}
