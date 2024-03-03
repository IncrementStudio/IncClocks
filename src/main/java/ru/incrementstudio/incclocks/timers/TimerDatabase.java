package ru.incrementstudio.incclocks.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import ru.incrementstudio.incapi.configs.Config;
import ru.incrementstudio.incapi.utils.ConfigUtil;
import ru.incrementstudio.incclocks.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimerDatabase {
    public static class StorageData {
        public String name;
        public Location location;
        public Vector u, v, d;
        public StorageData(String name, Location location, Vector u, Vector v, Vector d) {
            this.name = name;
            this.location = location;
            this.u = u;
            this.v = v;
            this.d = d;
        }
    }

    public static void add(String clocks, Location location, Vector U, Vector V, Vector D) {
        Config database = Main.getConfigManager().getConfig("timers");
        if (!database.get().contains(location.getWorld().getName()))
            database.get().createSection(location.getWorld().getName());
        ConfigurationSection worldSection = database.get().getConfigurationSection(location.getWorld().getName());
        worldSection.set(
                ConfigUtil.combinePath('|',
                        String.valueOf(location.getBlockX()),
                        String.valueOf(location.getBlockY()),
                        String.valueOf(location.getBlockZ())
                ),
                ConfigUtil.combinePath('|',
                        clocks,
                        String.valueOf(U.getBlockX()),
                        String.valueOf(U.getBlockY()),
                        String.valueOf(U.getBlockZ()),
                        String.valueOf(V.getBlockX()),
                        String.valueOf(V.getBlockY()),
                        String.valueOf(V.getBlockZ()),
                        String.valueOf(D.getBlockX()),
                        String.valueOf(D.getBlockY()),
                        String.valueOf(D.getBlockZ())
                )
        );
        database.save();
    }

    public static void remove(Location location) {
        Config database = Main.getConfigManager().getConfig("timers");
        String key = ConfigUtil.combinePath('|',
                String.valueOf(location.getBlockX()),
                String.valueOf(location.getBlockY()),
                String.valueOf(location.getBlockZ())
        );
        database.get().set(ConfigUtil.combinePath(location.getWorld().getName(), key), null);
        database.save();
    }

    public static Map<World, List<StorageData>> load() {
        Config database = Main.getConfigManager().getConfig("timers");
        Map<World, List<StorageData>> result = new HashMap<>();
        for (String world : database.get().getKeys(false)) {
            World worldObj = Bukkit.getWorld(world);
            if (worldObj == null) continue;
            ConfigurationSection worldSection = database.get().getConfigurationSection(world);
            for (String location : worldSection.getKeys(false)) {
                String[] locationData = location.split("\\|");
                if (locationData.length != 3) continue;
                Location locationObj;
                try {
                    locationObj = new Location(worldObj,
                            Integer.parseInt(locationData[0]),
                            Integer.parseInt(locationData[1]),
                            Integer.parseInt(locationData[2])
                    );
                } catch (NumberFormatException e) {
                    continue;
                }
                String[] data = database.get().getString(ConfigUtil.combinePath(world, location)).split("\\|");
                if (data.length != 10) continue;
                String name = data[0];
                Vector U, V, D;
                try {
                    U = new Vector(
                            Integer.parseInt(data[1]),
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3])
                    );
                    V = new Vector(
                            Integer.parseInt(data[4]),
                            Integer.parseInt(data[5]),
                            Integer.parseInt(data[6])
                    );
                    D = new Vector(
                            Integer.parseInt(data[7]),
                            Integer.parseInt(data[8]),
                            Integer.parseInt(data[9])
                    );
                } catch (NumberFormatException e) {
                    continue;
                }
                StorageData storageData = new StorageData(name, locationObj, U, V, D);
                if (!result.containsKey(worldObj))
                    result.put(worldObj, new ArrayList<>());
                result.get(worldObj).add(storageData);
            }
        }
        return result;
    }
}
