package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class Letter {
    @FunctionalInterface
    public interface RegionAction {
        void exec(int u, int v, int x, int y, int z);
    }
    private char currentValue = ' ';
    private Font font;
    private MaterialSet materialSet;
    private Location location;
    private World world;
    private BlockFace facing;
    private List<Block> blockList = new ArrayList<>();

    public Letter(Location location, BlockFace facing, Font font, MaterialSet materialSet) {
        this.location = location;
        world = location.getWorld();
        this.facing = facing;
        this.font = font;
        this.materialSet = materialSet;
    }

    public void setValue(char value) {
        currentValue = value;
        clear();
        set();
    }

    private void forRegion(RegionAction action) {
        if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
            for (int x = 0; x < font.getWidth(); x++) {
                for (int z = 0; z < font.getHeight(); z++) {
                    action.exec(x, z, location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z);
                }
            }
        }
    }

    public void clear() {
        for (Block block : blockList)
            block.setType(Material.AIR);
        blockList.clear();
    }

    private void set() {
        boolean[][] pattern = font.getByChar(currentValue);
        forRegion((u, v, x, y, z) -> {
            if (pattern[u][v]) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(materialSet.getDigits());
                blockList.add(block);
            }
        });
    }
}
