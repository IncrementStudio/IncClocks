package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clocks {
    public final Map<String, List<Number>> numbers = new HashMap<>();
    private final String format;
    private final int gap;
    private final GlyphSet glyphSet;
    private final MaterialSet materialSet;
    private final Location location;
    private final World world;
    private final BlockFace facing;

    public Clocks(String format, int gap, Location location, BlockFace facing, GlyphSet glyphSet, MaterialSet materialSet) {
        this.format = format;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.facing = facing;
        this.glyphSet = glyphSet;
        this.materialSet = materialSet;

        String[] formats = format.split(":");
        for (int i = 0; i < formats.length; i++) {
            switch (formats[i]) {
                case "min":
                    numbers.put(formats[i], new ArrayList<>());
                    numbers.get(formats[i]).add(
                            new Number(2, gap,
                            facing == BlockFace.UP ? location.clone().add(i * (glyphSet.getWidth() + gap), 0, 0) :
                            facing == BlockFace.DOWN ? location.clone().subtract(i * (glyphSet.getWidth() + gap), 0, 0) :
                            location,
                            facing, glyphSet, materialSet)
                    );
                    break;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Number number : numbers.get("min")) {
                    number.setValue(location.getWorld().getGameTime());
                }
//                for (Number number : numbers.get("hour")) {
//                    number.setValue();
//                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
}
