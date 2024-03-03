package ru.incrementstudio.incclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import ru.incrementstudio.incapi.configs.Config;
import ru.incrementstudio.incapi.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static class StorageClockData {
        public String clocks;
        public Location location;
        public Vector u, v, d;
        public StorageClockData(String clocks, Location location, Vector u, Vector v, Vector d) {
            this.clocks = clocks;
            this.location = location;
            this.u = u;
            this.v = v;
            this.d = d;
        }
    }

    public static void addClocks(String clocks, Location location, Vector U, Vector V, Vector D) {
        Config database = Main.getConfigManager().getConfig("database");
        StorageClockData storageClockData = new StorageClockData(clocks, location, U, V, D);
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

    public static void removeClocks(Location location) {
        Config database = Main.getConfigManager().getConfig("database");
        String key = ConfigUtil.combinePath('|',
                String.valueOf(location.getBlockX()),
                String.valueOf(location.getBlockY()),
                String.valueOf(location.getBlockZ())
        );
//        if (!database.get().contains(ConfigUtil.combinePath(location.getWorld().getName(), key)))
//            return;
        database.get().set(ConfigUtil.combinePath(location.getWorld().getName(), key), null);
        database.save();
    }

    public static Map<World, List<StorageClockData>> load() {
        Config database = Main.getConfigManager().getConfig("database");
        Map<World, List<StorageClockData>> result = new HashMap<>();
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
                StorageClockData storageClockData = new StorageClockData(name, locationObj, U, V, D);

                if (!result.containsKey(worldObj))
                    result.put(worldObj, new ArrayList<>());
                result.get(worldObj).add(storageClockData);
            }
        }
        return result;
    }
}
