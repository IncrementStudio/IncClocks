package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockString {
    private int length, gap;
    private final Font font;
    private final MaterialSet materialSet;
    private final Location location;
    private final World world;
    private Vector u, v;
    private final Letter[] letters;

    public BlockString(int length, int gap, Location location, Vector u, Vector v, Font font, MaterialSet materialSet) {
        this.length = length;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.font = font;
        this.materialSet = materialSet;
        letters = new Letter[length];
        this.u = u;
        this.v = v;

        for (int i = 0; i < letters.length; i++) {
            letters[i] = new Letter(location.clone().add(
                    u.getBlockX() * i * (font.getWidth() + gap),
                    u.getBlockY() * i * (font.getWidth() + gap),
                    u.getBlockZ() * i * (font.getWidth() + gap)),
                    u, v, font, materialSet);
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
