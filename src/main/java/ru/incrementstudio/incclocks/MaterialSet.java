package ru.incrementstudio.incclocks;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialSet {
    public MaterialSet(ConfigurationSection section) {
        if (section == null) return;
        if (section.contains("back")) back = Material.valueOf(section.getString("back"));
        if (section.contains("sides")) sides = Material.valueOf(section.getString("sides"));
    }
    private Material back = Material.BLACK_CONCRETE;
    private Material sides = Material.GRAY_CONCRETE;
    public Material getBack() {
        return back;
    }
    public Material getSides() {
        return sides;
    }
}
