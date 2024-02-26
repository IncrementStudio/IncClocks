package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class GlyphSet {
    private static HashMap<String, GlyphSet> glyphSets = new HashMap<>();
    private final File directory;
    private final BufferedImage[] glyphs = new BufferedImage[11];
    private final boolean[][][] set = new boolean[11][][];
    private int height = 0, width = 0;
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    private GlyphSet(String setName) throws IllegalArgumentException, IOException {
        directory = new File("plugins/IncDigitalClocks/glyphs/" + setName);
        if (!directory.exists())
            throw new IllegalArgumentException("Glyph set directory '" + setName + "' not found!");

        for (int i = 0; i < 10; i++) {
            File glyph = new File(directory.getPath() + "/" + i + ".png");
            if (!glyph.exists())
                throw new FileNotFoundException("Glyph file '" + glyph.getName() + "' not found in set '" + setName + "'!");
            glyphs[i] = ImageIO.read(glyph);
            if (glyphs[i].getWidth() > width)
                width = glyphs[i].getWidth();
            if (glyphs[i].getHeight() > height)
                height = glyphs[i].getHeight();
        }
        File separator = new File(directory.getPath() + "/separator.png");
        if (!separator.exists())
            throw new FileNotFoundException("Glyph file '" + separator.getName() + "' not found in set '" + setName + "'!");
        glyphs[10] = ImageIO.read(separator);
        if (glyphs[10].getHeight() > height)
            height = glyphs[10].getHeight();

        for (int i = 0; i < 10; i++) {
            BufferedImage glyph = glyphs[i];
            set[i] = new boolean[width][height];
            for (int x = 0; x < Math.min(width, glyph.getWidth()); x++)
                for (int y = 0; y < Math.min(height, glyph.getHeight()); y++)
                    set[i][x][y] = alpha(glyph.getRGB(x, y)) != 0;
        }
        BufferedImage separatorGlyph = glyphs[10];
        set[10] = new boolean[separatorGlyph.getWidth()][height];
        for (int x = 0; x < separatorGlyph.getWidth(); x++)
            for (int y = 0; y < Math.min(height, separatorGlyph.getHeight()); y++) {
                set[10][x][y] = alpha(separatorGlyph.getRGB(x, y)) != 0;
                System.out.println(x + ":" + y + " = " + alpha(separatorGlyph.getRGB(x, y)));
            }
    }

    static int alpha(int rgb) {
        return (0xff&(rgb>>24));
    }

    public static GlyphSet getSet(String name) {
        if (glyphSets.containsKey(name))
            return glyphSets.get(name);
        try {
            GlyphSet newSet = new GlyphSet(name);
            glyphSets.put(name, newSet);
            return newSet;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean[][] getByIndex(int index) {
        return set[index];
    }
}