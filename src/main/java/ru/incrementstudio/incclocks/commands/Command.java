package ru.incrementstudio.incclocks.commands;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incclocks.clocks.Clocks;
import ru.incrementstudio.incclocks.clocks.ClocksData;
import ru.incrementstudio.incclocks.clocks.ClocksDatabase;
import ru.incrementstudio.incclocks.Main;
import ru.incrementstudio.incclocks.stopwatches.Stopwatch;
import ru.incrementstudio.incclocks.stopwatches.StopwatchData;
import ru.incrementstudio.incclocks.stopwatches.StopwatchDatabase;
import ru.incrementstudio.incclocks.timers.Timer;
import ru.incrementstudio.incclocks.timers.TimerData;
import ru.incrementstudio.incclocks.timers.TimerDatabase;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (commandSender.hasPermission("incclocks.admin")) {
            if (strings.length == 1) {
                if (strings[0].equals("reload")) {
                    Main.getConfigManager().reloadAll();
                    for (String dir : new String[]{"fonts", "clocks", "timers", "stopwatches", "actions"}) {
                        File directory = new File("plugins/IncClocks/" + dir);
                        if (!directory.exists())
                            directory.mkdirs();
                    }
                    for (Clocks clock : Main.getInstance().getClocks())
                        clock.clear();
                    for (Timer timer : Main.getInstance().getTimers())
                        timer.clear();
                    for (Stopwatch stopwatch : Main.getInstance().getStopwatches())
                        stopwatch.clear();
                    Main.getInstance().getClocks().clear();
                    Main.getInstance().getTimers().clear();
                    Main.getInstance().getStopwatches().clear();
                    for (Map.Entry<World, List<ClocksDatabase.StorageData>> entry : ClocksDatabase.load().entrySet()) {
                        for (ClocksDatabase.StorageData data : entry.getValue()) {
                            File configFile = new File("plugins/IncClocks/clocks/" + data.name + ".yml");
                            if (!configFile.exists()) {
                                Main.logger().error("Часы '" + configFile.getName() + "' не найдены!");
                                continue;
                            }
                            new Clocks(new ClocksData(data.name, configFile), data.location, data.u, data.v, data.d);
                        }
                    }
                    for (Map.Entry<World, List<TimerDatabase.StorageData>> entry : TimerDatabase.load().entrySet()) {
                        for (TimerDatabase.StorageData data : entry.getValue()) {
                            File configFile = new File("plugins/IncClocks/timers/" + data.name + ".yml");
                            if (!configFile.exists()) {
                                Main.logger().error("Таймер '" + configFile.getName() + "' не найден!");
                                continue;
                            }
                            new Timer(new TimerData(data.name, configFile), data.location, data.u, data.v, data.d);
                        }
                    }
                    for (Map.Entry<World, List<StopwatchDatabase.StorageData>> entry : StopwatchDatabase.load().entrySet()) {
                        for (StopwatchDatabase.StorageData data : entry.getValue()) {
                            File configFile = new File("plugins/IncClocks/stopwatches/" + data.name + ".yml");
                            if (!configFile.exists()) {
                                Main.logger().error("Секундомер '" + configFile.getName() + "' не найден!");
                                continue;
                            }
                            new Stopwatch(new StopwatchData(data.name, configFile), data.location, data.u, data.v, data.d);
                        }
                    }
                    commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &aПлагин успешно перезагружен!"));
                    return true;
                }
            } else if (strings.length == 3) {
                if (strings[0].equals("place")) {
                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        if (strings[1].equals("clocks")) {
                            File clocksFile = new File("plugins/IncClocks/clocks/" + strings[2] + ".yml");
                            if (!clocksFile.exists()) {
                                commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cЧасы '&6" + strings[2] + "&c' не найдены!"));
                                return true;
                            } else {
                                Main.getInstance().getClocksPreview().addPlayer(new ClocksData(strings[2], clocksFile), player);
                                return true;
                            }
                        } else if (strings[1].equals("timer")) {
                            File timerFile = new File("plugins/IncClocks/timers/" + strings[2] + ".yml");
                            if (!timerFile.exists()) {
                                commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cТаймер '&6" + strings[2] + "&c' не найден!"));
                                return true;
                            } else {
                                Main.getInstance().getTimerPreview().addPlayer(new TimerData(strings[2], timerFile), player);
                                return true;
                            }
                        } else if (strings[1].equals("stopwatch")) {
                            File stopwatchFile = new File("plugins/IncClocks/stopwatches/" + strings[2] + ".yml");
                            if (!stopwatchFile.exists()) {
                                commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cСекундомер '&6" + strings[2] + "&c' не найден!"));
                                return true;
                            } else {
                                Main.getInstance().getStopwatchPreview().addPlayer(new StopwatchData(strings[2], stopwatchFile), player);
                                return true;
                            }
                        }
                    } else {
                        commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cЭта команда доступна только для игроков!"));
                        return true;
                    }
                }
            }
            commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &aПомощь"));
            commandSender.sendMessage(ColorUtil.toColor("&6/&eincclocks help &9- &b&oПомощь"));
            commandSender.sendMessage(ColorUtil.toColor("&6/&eincclocks place &6<&eclocks&6|&etimer&6|&estopwatch&6> &6<&ename&6> &9- &b&oУстановить часы&9&o/&b&oтаймер&9&o/&b&oсекундомер"));
            commandSender.sendMessage(ColorUtil.toColor("&6/&eincclocks reload &9- &b&oПерезагрузить плагин"));
        } else {
            commandSender.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cУ вас недостаточно полномочий, чтобы использовать эту команду!"));
            return true;
        }
        return true;
    }
}
