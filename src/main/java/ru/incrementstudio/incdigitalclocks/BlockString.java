package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockString {
    private int length, gap;
    private final Font font;
    private final Location location;
    private final World world;
    private Vector u, v;
    private final Letter[] letters;

    public BlockString(int length, int gap, Location location, Vector u, Vector v, Font font) {
        this.length = length;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.font = font;
        letters = new Letter[length];
        this.u = u;
        this.v = v;

        for (int i = 0; i < letters.length; i++) {
            letters[i] = new Letter(location.clone().add(
                    u.getBlockX() * i * (font.getWidth() + gap),
                    u.getBlockY() * i * (font.getWidth() + gap),
                    u.getBlockZ() * i * (font.getWidth() + gap)),
                    u, v, font, Material.AIR);
        }
    }

    public void setValue(java.lang.String value) {
        String nonColor = value.replaceAll("\\$\\{.*?}", "");
        Material material = Material.STONE;
        for (int i = 0; i < letters.length; i++) {
            if (value.charAt(i) == '$' && i < value.length() - 2) {
                if (value.charAt(i + 1) == '{') {
                    StringBuilder mat = new StringBuilder();
                    for (int j = i + 2; j < value.length(); j++) {
                        if (value.charAt(j) == '}') {
                            value = value.substring(0, i) + value.substring(j + 1);
                            break;
                        }
                        mat.append(value.charAt(j));
                    }
                    material = Material.valueOf(mat.toString());
                }
            }
            letters[i].setMaterial(material);
            letters[i].setValue(nonColor.charAt(i));
        }
    }

    public int getWidth() {
        int start = Math.abs((location.getBlockX() * u.getBlockX()) +
        (location.getBlockY() * u.getBlockY()) +
        (location.getBlockZ() * u.getBlockZ()));
        int end = Math.abs((letters[letters.length - 1].getLocation().getBlockX() * u.getBlockX()) +
        (letters[letters.length - 1].getLocation().getBlockY() * u.getBlockY()) +
        (letters[letters.length - 1].getLocation().getBlockZ() * u.getBlockZ()));
        int widthChar = font.getWidth();
        return Math.max(start, end) - Math.min(start, end) + widthChar;
    }

    public void clear() {
        for (Letter letter : letters)
            letter.clear();
    }
}
