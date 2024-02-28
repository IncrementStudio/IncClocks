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
import java.util.List;

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

    private List<Block> blockList = new ArrayList<>();
    private int paddingX, paddingY, width;
    private int offsetX, offsetY;

    public Clocks(String name, Location location, Vector u, Vector v, Vector d) throws FileNotFoundException {
        File configFile = new File("plugins/IncDigitalClocks/clocks/" + name + ".yml");
        if (!configFile.exists())
            throw new FileNotFoundException("Clocks '" + configFile.getName() + "' not found!");
        config = new Config(Main.getInstance(), configFile.getPath());

        timeType = config.get().contains("time") ? TimeType.valueOf(config.get().getString("time")) : TimeType.REAL;
        format = config.get().contains("format") ? config.get().getString("format") : "%h:%m";
        gap = config.get().contains("text.letter-spacing") ? config.get().getInt("text.letter-spacing") : -4;
        if (config.get().contains("form.padding")) {
            if (config.get().isConfigurationSection("form.padding")) {
                paddingX = config.get().contains("form.padding.horizontal") ? config.get().getInt("form.padding.horizontal") : 0;
                paddingY = config.get().contains("form.padding.vertical") ? config.get().getInt("form.padding.vertical") : 0;
            } else {
                paddingX = config.get().getInt("form.padding");
                paddingY = config.get().getInt("form.padding");
            }
        } else {
            paddingX = 0;
            paddingY = 0;
        }
        if (config.get().contains("text.offset")) {
            if (config.get().isConfigurationSection("text.offset")) {
                offsetX = config.get().contains("text.offset.x") ? config.get().getInt("text.offset.x") : 0;
                offsetY = config.get().contains("text.offset.y") ? config.get().getInt("text.offset.y") : 0;
            } else {
                offsetX = config.get().getInt("text.offset");
                offsetY = config.get().getInt("text.offset");
            }
        } else {
            offsetX = 0;
            offsetY = 0;
        }
        width = config.get().contains("form.width") ? config.get().getInt("form.width") : 3;
        this.location = location;
        world = location.getWorld();
        this.u = u;
        this.v = v;
        this.d = d;
        font = Font.getFont(config.get().contains("text.font") ? config.get().getString("text.font") : "", config.get().contains("text.text-size") ? (float) config.get().getDouble("text.text-size") : 16);
        this.materialSet = new MaterialSet(config.get().getConfigurationSection("materials"));

        timeString = new BlockString(format
                .replace("%h", "..")
                .replace("%m", "..")
                .replace("%s", "..")
                .length(), gap, getRelative(1 + offsetX + paddingX, 1 + offsetY + paddingY, width - 1), u, v, font, materialSet
        );

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

        for (int U = 0; U < paddingX * 2 + timeString.getWidth() + 2; U++) {
            for (int V = 0; V < paddingY * 2 + font.getHeight() + 2; V++) {
                for (int D = 0; D < width; D++) {
                    Location blockLocation = getRelative(U, V, D);
                    Block block = world.getBlockAt(blockLocation);
                    if (U == 0 || U == paddingX * 2 + timeString.getWidth() + 1 || V == 0 || V == paddingY * 2 + font.getHeight() + 1)
                        block.setType(materialSet.getSides());
                    else if (D < width - 1) {
                        block.setType(materialSet.getBack());
                    }
                    blockList.add(block);
                }
            }
        }
    }

    private Location getRelative(int U, int V, int D) {
        return location.clone().add(
                U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
        );
    }

    public void clear() {
        timeString.clear();
        for (Block block : blockList)
            block.setType(Material.AIR);
        blockList.clear();
    }
}
