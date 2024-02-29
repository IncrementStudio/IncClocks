package ru.incrementstudio.incdigitalclocks.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.incrementstudio.incapi.configs.Config;
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incdigitalclocks.ClocksPreview;
import ru.incrementstudio.incdigitalclocks.Main;

import java.io.File;
import java.io.FileNotFoundException;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length == 3) {
                if (strings[0].equals("place")) {
                    if (strings[1].equals("clocks")) {
                        File clocksFile = new File("plugins/IncDigitalClocks/clocks/" + strings[2] + ".yml");
                        if (!clocksFile.exists()) {
                            commandSender.sendMessage(ColorUtil.toColor("&cЧасы '&6" + strings[2] + "&c' не найдены!"));
                            return true;
                        }
                        ClocksPreview.addPlayer(player, strings[2]);
                    }
                }
            }
        }
        return true;
    }
}
