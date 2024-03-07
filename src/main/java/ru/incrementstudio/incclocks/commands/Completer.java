package ru.incrementstudio.incclocks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Completer implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("incclocks.admin")) return new ArrayList<>();
        if (strings.length == 1) {
            return List.of("place", "reload", "help");
        } else if (strings.length == 2) {
            if (strings[0].equals("place"))
                return List.of("clocks", "timer", "stopwatch");
        } else if (strings.length == 3) {
            if (strings[0].equals("place")) {
                if (strings[1].equals("clocks")) {
                    File clocksDirectory = new File("plugins/IncClocks/clocks");
                    if (!clocksDirectory.exists()) {
                        clocksDirectory.mkdirs();
                    }
                    return Arrays.stream(clocksDirectory.listFiles())
                            .map(File::getName)
                            .filter(x -> x.endsWith(".yml"))
                            .map(x -> x.substring(0, x.lastIndexOf('.')))
                            .collect(Collectors.toList());
                } else if (strings[1].equals("timer")) {
                    File timersDirectory = new File("plugins/IncClocks/timers");
                    if (!timersDirectory.exists()) {
                        timersDirectory.mkdirs();
                    }
                    return Arrays.stream(timersDirectory.listFiles())
                            .map(File::getName)
                            .filter(x -> x.endsWith(".yml"))
                            .map(x -> x.substring(0, x.lastIndexOf('.')))
                            .collect(Collectors.toList());
                } else if (strings[1].equals("stopwatch")) {
                    File stopwatchDirectory = new File("plugins/IncClocks/stopwatches");
                    if (!stopwatchDirectory.exists()) {
                        stopwatchDirectory.mkdirs();
                    }
                    return Arrays.stream(stopwatchDirectory.listFiles())
                            .map(File::getName)
                            .filter(x -> x.endsWith(".yml"))
                            .map(x -> x.substring(0, x.lastIndexOf('.')))
                            .collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}