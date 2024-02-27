package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

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
    public Location getLocation() {
        return location;
    }

    private World world;
    private Vector u, v;
    private List<Block> blockList = new ArrayList<>();

    public Letter(Location location, Vector u, Vector v, Font font, MaterialSet materialSet) {
        this.location = location;
        world = location.getWorld();
        this.u = u;
        this.v = v;
        this.font = font;
        this.materialSet = materialSet;
    }

    public void setValue(char value) {
        currentValue = value;
        clear();
        set();
    }

    private void forRegion(RegionAction action) {
        int UX = u.getBlockX(), UY = u.getBlockY(), UZ = u.getBlockZ(), VX = v.getBlockX(), VY = v.getBlockY(), VZ = v.getBlockZ();
        for (int x = 0; x < Math.max(Math.abs((font.getWidth() * UX) + (font.getHeight() * VX)), 1); x++) {
            for (int y = 0; y < Math.max(Math.abs((font.getWidth() * UY) + (font.getHeight() * VY)), 1); y++) {
                for (int z = 0; z < Math.max(Math.abs((font.getWidth() * UZ) + (font.getHeight() * VZ)), 1); z++) {
                    action.exec(
                            Math.abs((x * UX) + (y * UY) + (z * UZ)),
                            Math.abs((x * VX) + (y * VY) + (z * VZ)),
                            location.getBlockX() + x * (UX + VX),
                            location.getBlockY() + y * (UY + VY),
                            location.getBlockZ() + z * (UZ + VZ)
                    );
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
            if (pattern.length <= u) return;
            if (pattern[0].length <= v) return;
            if (pattern[u][pattern[u].length - 1 - v]) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(materialSet.getDigits());
                blockList.add(block);
            }
        });
    }
}
