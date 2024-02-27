package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ru.incrementstudio.incapi.Logger;
import ru.incrementstudio.incapi.configs.ConfigManager;
import ru.incrementstudio.incapi.utils.MathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
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

    private double angle = 0;
    public void rotate(double value) {
        this.angle += value;
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

        getServer().getPluginManager().registerEvents(new Rotator(), this);

        getCommand("clocks").setExecutor(new Command());

        try {
            clocks = new Clocks("default",
                    new Location(Bukkit.getWorld("world"), 0, 90, 0),
                    new Vector(configManager.getConfig("config").get().getInt("UX"), configManager.getConfig("config").get().getInt("UY"), configManager.getConfig("config").get().getInt("UZ")),
                    new Vector(configManager.getConfig("config").get().getInt("VX"), configManager.getConfig("config").get().getInt("VY"), configManager.getConfig("config").get().getInt("VZ")),
                    new Vector(configManager.getConfig("config").get().getInt("DX"), configManager.getConfig("config").get().getInt("DY"), configManager.getConfig("config").get().getInt("DZ"))
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Block target = player.getTargetBlock(null, 25);
                    BlockFace face = getBlockFace(player);
                    if (face != null) {
                        player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                Color.BLUE,
                                1f
                        ));
                        if (face == BlockFace.UP) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.WEST.getDirection().rotateAroundY(Math.toRadians(angle))).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.SOUTH.getDirection().rotateAroundY(Math.toRadians(angle))).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        }
                        if (face == BlockFace.DOWN) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.EAST.getDirection().rotateAroundY(-Math.toRadians(angle))).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.SOUTH.getDirection().rotateAroundY(-Math.toRadians(angle))).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        } else if (face == BlockFace.NORTH) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.WEST.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.UP.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        } else if (face == BlockFace.SOUTH) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.EAST.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.UP.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        } else if (face == BlockFace.EAST) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.NORTH.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.UP.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        } else if (face == BlockFace.WEST) {
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.SOUTH.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.LIME,
                                    1f
                            ));
                            player.spawnParticle(Particle.REDSTONE, target.getLocation().add(face.getDirection()).add(BlockFace.UP.getDirection()).add(0.5, 0.5, 0.5), 0, 1, 1, 1, new Particle.DustOptions(
                                    Color.RED,
                                    1f
                            ));
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 25);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    @Override
    public void onDisable() {
        clocks.clear();
    }
}
