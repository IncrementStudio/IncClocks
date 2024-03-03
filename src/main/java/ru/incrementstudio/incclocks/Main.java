package ru.incrementstudio.incclocks;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import ru.incrementstudio.incapi.Logger;
import ru.incrementstudio.incapi.configs.ConfigManager;
import ru.incrementstudio.incapi.menu.MenuListener;
import ru.incrementstudio.incclocks.commands.Command;
import ru.incrementstudio.incclocks.commands.Completer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private static ConfigManager configManager;
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    private static Logger logger;
    public static Logger logger() {
        return logger;
    }

    private List<Clocks> clocks = new ArrayList<>();
    public List<Clocks> getClocks() {
        return clocks;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);
        configManager = new ConfigManager(this, List.of("database"));
        configManager.updateAll();

        File clocksDirectory = new File("plugins/IncClocks/clocks");
        if (!clocksDirectory.exists()) {
            clocksDirectory.mkdirs();
        }
        File timersDirectory = new File("plugins/IncClocks/timers");
        if (!timersDirectory.exists()) {
            timersDirectory.mkdirs();
        }
        File stopwatchDirectory = new File("plugins/IncClocks/stopwatches");
        if (!stopwatchDirectory.exists()) {
            stopwatchDirectory.mkdirs();
        }
        File digitSetsDirectory = new File("plugins/IncClocks/fonts");
        if (!digitSetsDirectory.exists()) {
            digitSetsDirectory.mkdirs();
        }

        getServer().getPluginManager().registerEvents(new ClocksPreview(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("clocks").setExecutor(new Command());
        getCommand("clocks").setTabCompleter(new Completer());

        for (Map.Entry<World, List<Database.StorageClockData>> entry : Database.load().entrySet())
            for (Database.StorageClockData data : entry.getValue())
                new Clocks(data.clocks, data.location, data.u, data.v, data.d);
    }

    @Override
    public void onDisable() {
        for (Clocks clock : clocks)
            clock.clear();
        clocks.clear();
    }
}
