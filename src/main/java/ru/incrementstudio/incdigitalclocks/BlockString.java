package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class BlockString {
    private int length, gap;
    private final Font font;
    private final MaterialSet materialSet;
    private final Location location;
    private final World world;
    private final BlockFace facing;
    private final Letter[] letters;

    public BlockString(int length, int gap, Location location, BlockFace facing, Font font, MaterialSet materialSet) {
        this.length = length;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.facing = facing;
        this.font = font;
        this.materialSet = materialSet;
        letters = new Letter[length];
        for (int i = 0; i < letters.length; i++) {
            if (facing == BlockFace.UP)
                letters[i] = new Letter(location.clone().add(i * (font.getWidth() + gap), 0, 0), facing, font, materialSet);
            if (facing == BlockFace.DOWN)
                letters[i] = new Letter(location.clone().subtract(i * (font.getWidth() + gap), 0, 0), facing, font, materialSet);
        }
    }

    public void setValue(java.lang.String value) {
        for (int i = 0; i < letters.length; i++) {
            letters[i].setValue(value.charAt(i));
        }
    }

    public void clear() {
        for (Letter letter : letters)
            letter.clear();
    }
}
