package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.incrementstudio.incapi.Logger;
import ru.incrementstudio.incapi.configs.ConfigManager;
import ru.incrementstudio.incapi.utils.MathUtil;

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
        File digitSetsDirectory = new File("plugins/IncDigitalClocks/fonts");
        if (!digitSetsDirectory.exists()) {
            digitSetsDirectory.mkdirs();
        }

        getCommand("clocks").setExecutor(new Command());

        try {
            clocks = new Clocks("default", new Location(Bukkit.getWorld("world"), 0, 71, 0), BlockFace.UP);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        clocks.clear();
    }
}
