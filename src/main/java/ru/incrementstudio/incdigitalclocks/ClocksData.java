package ru.incrementstudio.incdigitalclocks;

import ru.incrementstudio.incapi.configs.Config;

import java.io.File;
import java.io.FileNotFoundException;

public class ClocksData {
    private final String name, format;
    private final int gap, width, uWidth;
    private int paddingX = 0, paddingY = 0;
    private final Font font;

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public int getGap() {
        return gap;
    }

    public int getWidth() {
        return width;
    }

    public int getuWidth() {
        return uWidth;
    }

    public int getPaddingX() {
        return paddingX;
    }

    public int getPaddingY() {
        return paddingY;
    }

    public Font getFont() {
        return font;
    }

    public ClocksData(String name) {
        File configFile = new File("plugins/IncDigitalClocks/clocks/" + name + ".yml");
        if (!configFile.exists()) {
            try {
                throw new FileNotFoundException("Clocks '" + configFile.getName() + "' not found!");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        Config config = new Config(Main.getInstance(), configFile.getPath());

        this.name = name;
        format = (config.get().contains("format") ? config.get().getString("format") : "%h:%m")
                .replace("%h", "..")
                .replace("%m", "..")
                .replace("%s", "..")
                .replaceAll("\\$\\{.*?}", "");
        gap = config.get().contains("text.letter-spacing") ? config.get().getInt("text.letter-spacing") : -4;

        if (config.get().contains("form.padding")) {
            if (config.get().isConfigurationSection("form.padding")) {
                paddingX = config.get().contains("form.padding.horizontal") ? config.get().getInt("form.padding.horizontal") : 0;
                paddingY = config.get().contains("form.padding.vertical") ? config.get().getInt("form.padding.vertical") : 0;
            } else {
                paddingX = config.get().getInt("form.padding");
                paddingY = config.get().getInt("form.padding");
            }
        }
        width = config.get().contains("form.width") ? config.get().getInt("form.width") : 3;
        font = Font.getFont(config.get().contains("text.font") ? config.get().getString("text.font") : "", config.get().contains("text.text-size") ? (float) config.get().getDouble("text.text-size") : 16);
        uWidth = font.getWidth() * format.length() + gap * (format.length() - 1);
    }
}
