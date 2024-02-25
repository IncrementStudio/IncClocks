package ru.incrementstudio.incdigitalclocks;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Config {
    private final Plugin plugin;
    private final File file;
    private final String fileName;
    private FileConfiguration config = null;

    public Config(Plugin plugin, String path) {
        this.plugin = plugin;
        this.file = new File(path);
        this.fileName = file.getPath().substring(file.getPath().indexOf("plugins" + File.separator + plugin.getName() + File.separator) + ("plugins" + File.separator + plugin.getName() + File.separator).length());
        reload();
    }

    public FileConfiguration get() {
        return config;
    }

    public void reload() {
        update();
        config = YamlConfiguration.loadConfiguration(file);
        setDefaultValues();
        save();
    }

    private void setDefaultValues() {
        FileConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(
                new InputStreamReader(
                        Objects.requireNonNull(plugin.getResource(fileName.replace(File.separator, "/")))));
        for (String key : defaultConfiguration.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfiguration.get(key));
            }
        }
    }

    public void save() {
        try {
            get().save(file);
        } catch (IOException ignored) {}
    }

    public void update() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
    }

    public Map<String, String> getDefines() {
        Map<String, String> result = new HashMap<>();
        if (!config.contains("defines")) return result;
        List<String> rawDefines = config.getStringList("defines");
        for (String rawDefine : rawDefines) {
            if (!rawDefine.contains("=")) continue;
            try {
                String key = rawDefine.substring(0, rawDefine.indexOf("="));
                if (key.isEmpty()) continue;
                String value = rawDefine.substring(rawDefine.indexOf("=") + 1);
                result.put(key, value);
            } catch (IndexOutOfBoundsException ignored) { }
        }
        return result;
    }

    public String applyDefines(String value) {
        for (Map.Entry<String, String> define : getDefines().entrySet()) {
            if (!value.contains("%" + define.getKey() + "%")) continue;
            value = value.replace("%" + define.getKey() + "%", define.getValue());
        }
        return value;
    }

    public List<String> applyDefines(List<String> value) {
        List<String> result = new ArrayList<>();
        for (String line : value)
            result.add(applyDefines(line));
        return result;
    }
}
