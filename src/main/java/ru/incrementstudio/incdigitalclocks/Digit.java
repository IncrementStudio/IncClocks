package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Digit {
    @FunctionalInterface
    public interface RegionAction {
        void exec(int u, int v, int x, int y, int z);
    }
    private int currentValue = 0;
    private GlyphSet glyphSet;
    private MaterialSet materialSet;
    private Location location;
    private World world;
    private BlockFace facing;

    public Digit(Location location, BlockFace facing, GlyphSet glyphSet, MaterialSet materialSet) {
        this.location = location;
        world = location.getWorld();
        this.facing = facing;
        this.glyphSet = glyphSet;
        this.materialSet = materialSet;
    }

    public void setValue(int value) {
        currentValue = value;
        clear();
        set();
    }

    private void forRegion(RegionAction action) {
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            for (int x = 0; x < glyphSet.getWidth(); x++) {
                for (int z = 0; z < glyphSet.getHeight(); z++) {
                    action.exec(x, z, location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z);
                }
            }
        }
    }

    private void clear() {
        forRegion((u, v, x, y, z) -> {
            Block block = world.getBlockAt(x, y, z);
            block.setType(Material.AIR);
        });
    }

    private void set() {
        boolean[][] pattern = glyphSet.getByIndex(currentValue);
        forRegion((u, v, x, y, z) -> {
            if (pattern[u][v]) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(materialSet.getDigits());
            }
        });
    }
}
