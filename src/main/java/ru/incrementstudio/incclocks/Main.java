package ru.incrementstudio.incclocks;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import ru.incrementstudio.incapi.Logger;
import ru.incrementstudio.incapi.configs.ConfigManager;
import ru.incrementstudio.incapi.menu.MenuListener;
import ru.incrementstudio.incclocks.clocks.Clocks;
import ru.incrementstudio.incclocks.clocks.ClocksData;
import ru.incrementstudio.incclocks.clocks.ClocksPreview;
import ru.incrementstudio.incclocks.clocks.ClocksDatabase;
import ru.incrementstudio.incclocks.stopwatches.Stopwatch;
import ru.incrementstudio.incclocks.stopwatches.StopwatchData;
import ru.incrementstudio.incclocks.stopwatches.StopwatchDatabase;
import ru.incrementstudio.incclocks.stopwatches.StopwatchPreview;
import ru.incrementstudio.incclocks.timers.Timer;
import ru.incrementstudio.incclocks.commands.Command;
import ru.incrementstudio.incclocks.commands.Completer;
import ru.incrementstudio.incclocks.timers.TimerData;
import ru.incrementstudio.incclocks.timers.TimerDatabase;
import ru.incrementstudio.incclocks.timers.TimerPreview;

import java.io.File;
import java.io.FileNotFoundException;
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

    private final List<Clocks> clocks = new ArrayList<>();
    public List<Clocks> getClocks() {
        return clocks;
    }
    private final List<Timer> timers = new ArrayList<>();
    public List<Timer> getTimers() {
        return timers;
    }
    private final List<Stopwatch> stopwatches = new ArrayList<>();
    public List<Stopwatch> getStopwatches() {
        return stopwatches;
    }

    private final ClocksPreview clocksPreview = new ClocksPreview();
    public ClocksPreview getClocksPreview() {
        return clocksPreview;
    }
    private final TimerPreview timerPreview = new TimerPreview();
    public TimerPreview getTimerPreview() {
        return timerPreview;
    }
    private final StopwatchPreview stopwatchPreview = new StopwatchPreview();
    public StopwatchPreview getStopwatchPreview() {
        return stopwatchPreview;
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = new Logger(this);
        configManager = new ConfigManager(this, List.of("clocks", "timers", "stopwatches"));
        configManager.updateAll();

        for (String dir : new String[]{"fonts", "clocks", "timers", "stopwatches", "actions"}) {
            File directory = new File("plugins/IncClocks/" + dir);
            if (!directory.exists())
                directory.mkdirs();
        }

        getServer().getPluginManager().registerEvents(clocksPreview, this);
        getServer().getPluginManager().registerEvents(timerPreview, this);
        getServer().getPluginManager().registerEvents(stopwatchPreview, this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("incclocks").setExecutor(new Command());
        getCommand("incclocks").setTabCompleter(new Completer());

        for (Map.Entry<World, List<ClocksDatabase.StorageData>> entry : ClocksDatabase.load().entrySet()) {
            for (ClocksDatabase.StorageData data : entry.getValue()) {
                File configFile = new File("plugins/IncClocks/clocks/" + data.name + ".yml");
                if (!configFile.exists()) {
                    logger.error("Часы '" + configFile.getName() + "' не найдены!");
                    continue;
                }
                new Clocks(new ClocksData(data.name, configFile), data.location, data.u, data.v, data.d);
            }
        }
        for (Map.Entry<World, List<TimerDatabase.StorageData>> entry : TimerDatabase.load().entrySet()) {
            for (TimerDatabase.StorageData data : entry.getValue()) {
                File configFile = new File("plugins/IncClocks/timers/" + data.name + ".yml");
                if (!configFile.exists()) {
                    logger.error("Таймер '" + configFile.getName() + "' не найден!");
                    continue;
                }
                new Timer(new TimerData(data.name, configFile), data.location, data.u, data.v, data.d);
            }
        }
        for (Map.Entry<World, List<StopwatchDatabase.StorageData>> entry : StopwatchDatabase.load().entrySet()) {
            for (StopwatchDatabase.StorageData data : entry.getValue()) {
                File configFile = new File("plugins/IncClocks/stopwatches/" + data.name + ".yml");
                if (!configFile.exists()) {
                    logger.error("Секундомер '" + configFile.getName() + "' не найден!");
                    continue;
                }
                new Stopwatch(new StopwatchData(data.name, configFile), data.location, data.u, data.v, data.d);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Clocks clock : clocks)
            clock.clear();
        for (Timer timer : timers)
            timer.clear();
        for (Stopwatch stopwatch : stopwatches)
            stopwatch.clear();
        clocks.clear();
        timers.clear();
        stopwatches.clear();
    }
}
