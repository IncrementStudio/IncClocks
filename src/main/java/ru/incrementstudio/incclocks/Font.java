package ru.incrementstudio.incclocks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font {
    private static Map<String, Map<Float, Font>> fonts = new HashMap<>();
    private final File fontFile;
    private final Map<Character, BufferedImage> glyphs = new HashMap<>();
    private final Map<Character, boolean[][]> set = new HashMap<>();
    private final float size;
    private final int width, height;
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    private final java.awt.Font font;

    private Font(String setName, float size) throws IllegalArgumentException {
        this.size = size;
        fontFile = new File("plugins/IncClocks/fonts/" + setName + ".ttf");
        if (!fontFile.exists())
            throw new IllegalArgumentException("Font '" + fontFile.getName() + "' not found!");
        try {
            font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile).deriveFont(size);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        width = fm.charWidth('W');
        height = fm.getHeight();
        g2d.dispose();
    }

    static int alpha(int rgb) {
        return (0xff&(rgb>>24));
    }

    public static Font getFont(String name, float size) {
        if (fonts.containsKey(name)) {
            if (fonts.get(name).containsKey(size)) {
                return fonts.get(name).get(size);
            } else {
                Font newFont = new Font(name, size);
                fonts.get(name).put(size, newFont);
                return newFont;
            }
        } else {
            Map<Float, Font> newFontSet = new HashMap<>();
            Font newFont = new Font(name, size);
            newFontSet.put(size, newFont);
            fonts.put(name, newFontSet);
            return newFont;
        }
    }

    public boolean[][] getByChar(char character) {
        if (set.containsKey(character))
            return set.get(character);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        g2d.drawString(character + "",
                (width - fm.charWidth(character)) / 2,
                fm.getAscent());
        g2d.dispose();

        glyphs.put(character, img);

        boolean[][] map = new boolean[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++)
            for (int y = 0; y < img.getHeight(); y++)
                map[x][y] = alpha(img.getRGB(x, y)) != 0;
        set.put(character, map);
        return map;
    }
}