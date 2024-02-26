package ru.incrementstudio.incdigitalclocks;

import ru.incrementstudio.incapi.configs.Config;

import java.util.List;

public class DigitSet {
    private final Config config;
    private final boolean[][][] set = new boolean[11][][];
    private final int height, width;
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    public DigitSet(String name) {
        config = new Config(Main.getInstance(), "plugins/IncDigitalClocks/digitSets/" + name + ".yml");
        height = config.get().getInt("height");
        width = config.get().getInt("width");
        List<String>[] setData = new List[] {
            config.get().getStringList("0"),
            config.get().getStringList("1"),
            config.get().getStringList("2"),
            config.get().getStringList("3"),
            config.get().getStringList("4"),
            config.get().getStringList("5"),
            config.get().getStringList("6"),
            config.get().getStringList("7"),
            config.get().getStringList("8"),
            config.get().getStringList("9"),
            config.get().getStringList("separator"),
        };
        for (int i = 0; i < 11; i++) {
            List<String> digitData = setData[i];
            set[i] = new boolean[height][];
            for (int j = 0; j < Math.min(height, digitData.size()); j++) {
                String line = digitData.get(j);
                if (line.length() < width)
                    line += " ".repeat(width - line.length());
                set[i][j] = new boolean[width];
                for (int k = 0; k < Math.min(width, line.length()); k++) {
                    set[i][j][k] = line.charAt(k) != ' ';
                }
            }
        }
    }

    public boolean[][] getByIndex(int index) {
        return set[index];
    }
}