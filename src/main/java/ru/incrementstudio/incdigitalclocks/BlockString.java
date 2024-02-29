package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class BlockString {
    private int length, gap;
    private final Font font;
    private final Location location;
    private final World world;
    private Vector u, v;
    private final Letter[] letters;

    public BlockString(Clocks clocks, int length, int gap, Location location, Vector u, Vector v, Font font) {
        this.length = length;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.font = font;
        letters = new Letter[length];
        this.u = u;
        this.v = v;

        for (int i = 0; i < letters.length; i++) {
            letters[i] = new Letter(clocks, location.clone().add(
                    u.getBlockX() * i * (font.getWidth() + gap),
                    u.getBlockY() * i * (font.getWidth() + gap),
                    u.getBlockZ() * i * (font.getWidth() + gap)),
                    u, v, font, Material.AIR);
        }
    }

    public void setValue(java.lang.String value) {
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
            letters[i].setValue(value.charAt(i));
        }
    }

    public int getWidth() {
        return font.getWidth() * letters.length + gap * (letters.length - 1);
    }

    public void clear() {
        for (Letter letter : letters)
            letter.clear();
    }
}
