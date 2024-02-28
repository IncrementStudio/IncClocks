package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialSet {
    public MaterialSet(ConfigurationSection section) {
        if (section.contains("text")) text = Material.valueOf(section.getString("text"));
        if (section.contains("back")) back = Material.valueOf(section.getString("back"));
        if (section.contains("sides")) sides = Material.valueOf(section.getString("sides"));
    }
    private Material text = Material.GREEN_WOOL;
    private Material back = Material.BLACK_WOOL;
    private Material sides = Material.GRAY_WOOL;
    public Material getText() {
        return text;
    }
    public Material getBack() {
        return back;
    }
    public Material getSides() {
        return sides;
    }
}
