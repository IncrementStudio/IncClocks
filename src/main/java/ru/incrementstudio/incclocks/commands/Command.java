package ru.incrementstudio.incclocks.commands;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incclocks.Clocks;
import ru.incrementstudio.incclocks.ClocksPreview;
import ru.incrementstudio.incclocks.Database;
import ru.incrementstudio.incclocks.Main;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (commandSender.hasPermission("clocks.admin")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (strings.length == 3) {
                    if (strings[0].equals("place")) {
                        if (strings[1].equals("clocks")) {
                            File clocksFile = new File("plugins/IncClocks/clocks/" + strings[2] + ".yml");
                            if (!clocksFile.exists()) {
                                commandSender.sendMessage(ColorUtil.toColor("&9[&bIncBosses&9] &cЧасы '&6" + strings[2] + "&c' не найдены!"));
                                return true;
                            } else {
                                ClocksPreview.addPlayer(player, strings[2]);
                                return true;
                            }
                        }
                    }
                } else if (strings.length == 1) {
                    if (strings[0].equals("reload")) {
                        for (Clocks clock : Main.getInstance().getClocks())
                            clock.clear();
                        Main.getInstance().getClocks().clear();
                        for (Map.Entry<World, List<Database.StorageClockData>> entry : Database.load().entrySet())
                            for (Database.StorageClockData data : entry.getValue())
                                new Clocks(data.clocks, data.location, data.u, data.v, data.d);
                        return true;
                    }
                }
            }
        }
        return true;
    }
}
