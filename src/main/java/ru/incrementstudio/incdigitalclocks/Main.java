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

    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);
        configManager = new ConfigManager(this, List.of("config"));

        File clocksDirectory = new File("plugins/IncDigitalClocks/clocks");
        if (!clocksDirectory.exists()) {
            clocksDirectory.mkdirs();
        }
        File digitSetsDirectory = new File("plugins/IncDigitalClocks/glyphs");
        if (!digitSetsDirectory.exists()) {
            digitSetsDirectory.mkdirs();
        }

        getCommand("clocks").setExecutor(new Command());

        Number hours = new Number(2, 1, new Location(Bukkit.getWorld("world"), 0, 71, 0), BlockFace.UP, GlyphSet.getSet("minecraft"), new MaterialSet());
        Number mins = new Number(2, 1, new Location(Bukkit.getWorld("world"), 0, 71, 8), BlockFace.UP, GlyphSet.getSet("minecraft"), new MaterialSet());
        new BukkitRunnable() {
            @Override
            public void run() {
                hours.setValue(
                        Bukkit.getWorld("world").getTime() / 1000 +
                                (Bukkit.getWorld("world").getTime() / 1000 + 6 < 24 ? 6 : -18)
                );
                mins.setValue(
                        (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, Bukkit.getWorld("world").getTime() % 1000))
                );
                Bukkit.getWorld("world").setTime(Bukkit.getWorld("world").getTime() + 5);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    @Override
    public void onDisable() {
    }
}
