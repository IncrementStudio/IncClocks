package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.incrementstudio.incapi.configs.Config;
import ru.incrementstudio.incapi.utils.MathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

public class Clocks {
    private enum TimeType {
        REAL, GAME
    }
    private final Config config;
    private final TimeType timeType;
    private final java.lang.String format;
    private final int gap;
    private final Font font;
    private final MaterialSet materialSet;
    private final Location location;
    private final World world;
    private final BlockString timeString;
    private Vector u, v, d;
    private Block start, end, center;

    public Clocks(String name, Location location, Vector u, Vector v, Vector d) throws FileNotFoundException {
        File configFile = new File("plugins/IncDigitalClocks/clocks/" + name + ".yml");
        if (!configFile.exists())
            throw new FileNotFoundException("Clocks '" + configFile.getName() + "' not found!");
        config = new Config(Main.getInstance(), configFile.getPath());

        timeType = config.get().contains("time") ? TimeType.valueOf(config.get().getString("time")) : TimeType.REAL;
        format = config.get().contains("format") ? config.get().getString("format") : "%h:%m";
        gap = config.get().contains("letter-spacing") ? config.get().getInt("letter-spacing") : -4;
        this.location = location;
        world = location.getWorld();
        this.u = u;
        this.v = v;
        this.d = d;
        font = Font.getFont(config.get().contains("font") ? config.get().getString("font") : "", config.get().contains("text-size") ? (float) config.get().getDouble("text-size") : 16);
        this.materialSet = new MaterialSet();

        timeString = new BlockString(format
                .replace("%h", "..")
                .replace("%m", "..")
                .replace("%s", "..")
                .length(), gap, location, u, v, font, materialSet
        );

        start = world.getBlockAt(location);
        start.setType(Material.LIME_WOOL);
        end = world.getBlockAt(location.clone().add(
                timeString.getWidth() * u.getBlockX(),
                timeString.getWidth() * u.getBlockY(),
                timeString.getWidth() * u.getBlockZ()
        ).add(
                font.getHeight() * v.getBlockX(),
                font.getHeight() * v.getBlockY(),
                font.getHeight() * v.getBlockZ()
        ));
        end.setType(Material.RED_WOOL);
        center = world.getBlockAt(location.clone().add(
                timeString.getWidth() * u.getBlockX() / 2.0,
                timeString.getWidth() * u.getBlockY() / 2.0,
                timeString.getWidth() * u.getBlockZ() / 2.0
        ).add(
                font.getHeight() * v.getBlockX() / 2.0,
                font.getHeight() * v.getBlockY() / 2.0,
                font.getHeight() * v.getBlockZ() / 2.0
        ).add(d));
        center.setType(Material.BLUE_WOOL);

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (timeType) {
                    case GAME:
                        long hoursG = Bukkit.getWorld("world").getTime() / 1000 +
                                (Bukkit.getWorld("world").getTime() / 1000 + 6 < 24 ? 6 : -18);
                        long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, Bukkit.getWorld("world").getTime() % 1000));
                        timeString.setValue(
                                format
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                        );
                        break;
                    case REAL:
                        long hoursR = new Date(System.currentTimeMillis()).getHours();
                        long minutesR = new Date(System.currentTimeMillis()).getMinutes();
                        long secondsR = new Date(System.currentTimeMillis()).getSeconds();
                        timeString.setValue(
                                format
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                        .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                        );
                        break;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public void clear() {
        timeString.clear();
        start.setType(Material.AIR);
        end.setType(Material.AIR);
        center.setType(Material.AIR);
    }
}
