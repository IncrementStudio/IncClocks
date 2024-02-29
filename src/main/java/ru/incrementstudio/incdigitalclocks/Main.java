package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import ru.incrementstudio.incapi.Logger;
import ru.incrementstudio.incapi.configs.ConfigManager;
import ru.incrementstudio.incapi.menu.MenuListener;
import ru.incrementstudio.incdigitalclocks.commands.Command;
import ru.incrementstudio.incdigitalclocks.commands.Completer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

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

    Clocks clocks;

    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);
        configManager = new ConfigManager(this, List.of("config"));

        File clocksDirectory = new File("plugins/IncDigitalClocks/clocks");
        if (!clocksDirectory.exists()) {
            clocksDirectory.mkdirs();
        }
        File timersDirectory = new File("plugins/IncDigitalClocks/timers");
        if (!timersDirectory.exists()) {
            timersDirectory.mkdirs();
        }
        File stopwatchDirectory = new File("plugins/IncDigitalClocks/stopwatch");
        if (!stopwatchDirectory.exists()) {
            stopwatchDirectory.mkdirs();
        }
        File digitSetsDirectory = new File("plugins/IncDigitalClocks/fonts");
        if (!digitSetsDirectory.exists()) {
            digitSetsDirectory.mkdirs();
        }

        getServer().getPluginManager().registerEvents(new ClocksPreview(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("clocks").setExecutor(new Command());
        getCommand("clocks").setTabCompleter(new Completer());

        try {
            clocks = new Clocks("default",
                    new Location(Bukkit.getWorld("world"), 130, 64, -97),
                    new Vector(configManager.getConfig("config").get().getInt("UX"), configManager.getConfig("config").get().getInt("UY"), configManager.getConfig("config").get().getInt("UZ")),
                    new Vector(configManager.getConfig("config").get().getInt("VX"), configManager.getConfig("config").get().getInt("VY"), configManager.getConfig("config").get().getInt("VZ")),
                    new Vector(configManager.getConfig("config").get().getInt("DX"), configManager.getConfig("config").get().getInt("DY"), configManager.getConfig("config").get().getInt("DZ"))
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        clocks.clear();
    }
}
