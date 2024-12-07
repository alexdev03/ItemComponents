package org.alexdev.itemcomponents;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import org.alexdev.itemcomponents.commands.MainCommand;
import org.alexdev.itemcomponents.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

@Getter
public final class ItemComponents extends JavaPlugin {

    private ConfigManager configManager;
    private TaskScheduler taskScheduler;


    @Override
    public void onEnable() {

        taskScheduler = UniversalScheduler.getScheduler(this);
        configManager = new ConfigManager(this);
        if (!loadConfig()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        loadCommands();
        loadStats();

        getLogger().info("ItemComponents has been enabled!");
    }

    private boolean loadConfig() {
        final Optional<Throwable> error = configManager.loadConfigs();
        if (error.isPresent()) {
            getLogger().log(java.util.logging.Level.SEVERE, "Failed to load configuration", error.get());
            return false;
        }
        return true;
    }

    private void loadCommands() {
        final MainCommand command = new MainCommand(this);
        command.registerCommands();
    }

    private void loadStats() {
//        final Metrics metrics = new org.bstats.bukkit.Metrics(this, -1);
    }


    @Override
    public void onDisable() {
        taskScheduler.cancelTasks();
    }

}
