package ru.incrementstudio.incclocks.bases;

import ru.incrementstudio.incapi.configs.Config;
import ru.incrementstudio.incclocks.Font;
import ru.incrementstudio.incclocks.Main;
import ru.incrementstudio.incclocks.MaterialSet;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class BaseData {
    public enum TimeType {
        REAL, GAME
    }
    protected final String name, format;
    protected final Config config;
    protected final TimeType timeType;
    protected final int offsetX, offsetY;
    protected final MaterialSet materialSet;
    protected final int gap, width, borderRadius;
    protected final int paddingX, paddingY;
    protected final Font font;

    public String getName() {
        return name;
    }
    public String getFormat() {
        return format;
    }
    public String getFormatWithoutFormatting() {
        return format
                .replace("%h", "00")
                .replace("%m", "00")
                .replace("%s", "00")
                .replaceAll("\\$\\{.*?}", "");
    }
    public int getGap() {
        return gap;
    }
    public int getWidth() {
        return width;
    }
    public Config getConfig() {
        return config;
    }
    public TimeType getTimeType() {
        return timeType;
    }
    public int getOffsetX() {
        return offsetX;
    }
    public int getOffsetY() {
        return offsetY;
    }
    public MaterialSet getMaterialSet() {
        return materialSet;
    }
    public int getBorderRadius() {
        return borderRadius;
    }
    public int getUWidth() {
        return font.getWidth() * getFormatWithoutFormatting().length() + gap * (getFormatWithoutFormatting().length() - 1);
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

    public BaseData(String name, File configFile) {
        this.name = name;
        config = new Config(Main.getInstance(), configFile.getPath());

        timeType = config.get().contains("time-type") ? TimeType.valueOf(config.get().getString("time-type")) : TimeType.REAL;
        format = config.get().contains("format") ? config.get().getString("format") : "${LIME_CONCRETE}%h:%m";
        gap = config.get().contains("text.letter-spacing") ? config.get().getInt("text.letter-spacing") : 0;
        if (config.get().contains("form.padding")) {
            if (config.get().isConfigurationSection("form.padding")) {
                paddingX = config.get().contains("form.padding.horizontal") ? config.get().getInt("form.padding.horizontal") : 0;
                paddingY = config.get().contains("form.padding.vertical") ? config.get().getInt("form.padding.vertical") : 0;
            } else {
                paddingX = config.get().getInt("form.padding");
                paddingY = config.get().getInt("form.padding");
            }
        } else {
            paddingX = 0;
            paddingY = 0;
        }
        if (config.get().contains("text.offset")) {
            if (config.get().isConfigurationSection("text.offset")) {
                offsetX = config.get().contains("text.offset.x") ? config.get().getInt("text.offset.x") : 0;
                offsetY = config.get().contains("text.offset.y") ? config.get().getInt("text.offset.y") : 0;
            } else {
                offsetX = config.get().getInt("text.offset");
                offsetY = config.get().getInt("text.offset");
            }
        } else {
            offsetX = 0;
            offsetY = 0;
        }
        width = config.get().contains("form.width") ? Math.max(config.get().getInt("form.width"), 3) : 5;
        font = Font.getFont(config.get().contains("text.font") ? config.get().getString("text.font") : "", config.get().contains("text.text-size") ? (float) config.get().getDouble("text.text-size") : 16);
        materialSet = new MaterialSet(config.get().getConfigurationSection("materials"));
        borderRadius = Math.min((paddingX * 2 + font.getWidth() * getFormatWithoutFormatting().length() + gap * (getFormatWithoutFormatting().length() - 1) + 2) / 2,
                Math.min((paddingY * 2 + font.getHeight() + 2) / 2,
                        config.get().contains("form.border-radius") ? config.get().getInt("form.border-radius") : 0));
    }
}
