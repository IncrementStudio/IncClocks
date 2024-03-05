package ru.incrementstudio.incclocks.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.incrementstudio.incapi.menu.Button;
import ru.incrementstudio.incapi.menu.Menu;
import ru.incrementstudio.incapi.menu.Page;
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incapi.utils.MathUtil;
import ru.incrementstudio.incapi.utils.builders.ItemBuilder;
import ru.incrementstudio.incclocks.Main;
import ru.incrementstudio.incclocks.bases.Base;
import ru.incrementstudio.incclocks.bases.BaseData;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timer extends Base {
    private final TimerData timerData;
    private BukkitTask updateTimeTask;
    private boolean isWorking = false;
    private long startTime;
    private final long timerTime;

    public Timer(TimerData data, Location location, Vector u, Vector v, Vector d) {
        super(data, location, u, v, d);
        timerData = data;
        timerTime = timerData.getTime();
        switch (timerData.getTimeType()) {
            case GAME:
                long time = timerTime % 24000;
                long daysG = timerTime / 24000;
                long hoursG = time / 1000;
                long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, time % 1000));
                timeString.setValue(
                        data.getFormat()
                                .replace("%d", "0".repeat(2 - String.valueOf(daysG).length()) + daysG)
                                .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                );
                break;
            case REAL:
                long daysR = timerTime / (1000 * 60 * 60 * 24);
                long hoursR = (timerTime / (1000 * 60 * 60)) % 24;
                long minutesR = (timerTime / (1000 * 60)) % 60;
                long secondsR = (timerTime / 1000) % 60;
                timeString.setValue(
                        data.getFormat()
                                .replace("%d", "0".repeat(2 - String.valueOf(daysR).length()) + daysR)
                                .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                );
                break;
        }
        Main.getInstance().getTimers().add(this);
    }

    public void clear() {
        if (updateTimeTask != null)
            updateTimeTask.cancel();
        super.clear();
    }

    @Override
    public void onBreak(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("clocks.admin")) {
            Menu menu = new Menu();
            menu.addPage(
                    new Page("Таймер", 45)
                            .setSlots(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                            .setName(" ")
                                            .build(),
                                    0, 1, 2, 3, 4, 5, 6, 7, 8,
                                    9, 13, 17,
                                    18, 22, 26,
                                    27, 31, 35,
                                    36, 37, 38, 39, 40, 41, 42, 43, 44
                            ).setSlots(new Button(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                                               .setName("&a&lСТАРТ")
                                               .build()) {
                                           @Override
                                           public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                               if (isWorking) return;
                                               isWorking = true;
                                               switch (data.getTimeType()) {
                                                   case REAL:
                                                       startTime = System.currentTimeMillis();
                                                       break;
                                                   case GAME:
                                                       startTime = world.getGameTime();
                                                       break;
                                               }
                                               updateTimeTask = new BukkitRunnable() {
                                                   @Override
                                                   public void run() {
                                                       if (world.getGameTime() >= startTime + timerTime) {
                                                           isWorking = false;
                                                           onStop();
                                                           cancel();
                                                           return;
                                                       }
                                                       switch (timerData.getTimeType()) {
                                                           case GAME:
                                                               long currentTimeG = startTime + timerTime - world.getGameTime();
                                                               long time = currentTimeG % 24000;
                                                               long daysG = currentTimeG / 24000;
                                                               long hoursG = time / 1000;
                                                               long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, time % 1000));
                                                               timeString.setValue(
                                                                       data.getFormat()
                                                                               .replace("%d", "0".repeat(2 - String.valueOf(daysG).length()) + daysG)
                                                                               .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                                                               .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                                                               );
                                                               break;
                                                           case REAL:
                                                               long currentTimeR = startTime + timerTime - System.currentTimeMillis();
                                                               long daysR = currentTimeR / (1000 * 60 * 60 * 24);
                                                               long hoursR = (currentTimeR / (1000 * 60 * 60)) % 24;
                                                               long minutesR = (currentTimeR / (1000 * 60)) % 60;
                                                               long secondsR = (currentTimeR / 1000) % 60;
                                                               timeString.setValue(
                                                                       data.getFormat()
                                                                               .replace("%d", "0".repeat(2 - String.valueOf(daysR).length()) + daysR)
                                                                               .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                                                               .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                                                               .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                                                               );
                                                               break;
                                                       }
                                                   }
                                               }.runTaskTimer(Main.getInstance(), 0L, 1L);
                                               player.closeInventory();
                                           }
                                       },
                                    14, 15, 16,
                                    23, 24, 25,
                                    32, 33, 34
                            ).setSlots(new Button(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                                               .setName("&c&lСБРОС")
                                               .build()) {
                                           @Override
                                           public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                               if (!isWorking) return;
                                               isWorking = false;
                                               updateTimeTask.cancel();
                                               switch (timerData.getTimeType()) {
                                                   case GAME:
                                                       long time = timerTime % 24000;
                                                       long daysG = timerTime / 24000;
                                                       long hoursG = time / 1000;
                                                       long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, time % 1000));
                                                       timeString.setValue(
                                                               data.getFormat()
                                                                       .replace("%d", "0".repeat(2 - String.valueOf(daysG).length()) + daysG)
                                                                       .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                                                       .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                                                       );
                                                       break;
                                                   case REAL:
                                                       long daysR = timerTime / (1000 * 60 * 60 * 24);
                                                       long hoursR = (timerTime / (1000 * 60 * 60)) % 24;
                                                       long minutesR = (timerTime / (1000 * 60)) % 60;
                                                       long secondsR = (timerTime / 1000) % 60;
                                                       timeString.setValue(
                                                               data.getFormat()
                                                                       .replace("%d", "0".repeat(2 - String.valueOf(daysR).length()) + daysR)
                                                                       .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                                                       .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                                                       .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                                                       );
                                                       break;
                                               }
                                               player.closeInventory();
                                           }
                                       },
                                    10, 11, 12,
                                    19, 20, 21,
                                    28, 29, 30
                            ).setSlots(new Button(new ItemBuilder(Material.RED_WOOL)
                                               .setName("&c&lУБРАТЬ")
                                               .build()) {
                                           @Override
                                           public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                               menu.show(player, 1);
                                           }
                                       }, 8
                            ).apply()
            ).addPage(
                    new Page("Вы хотите убрать таймер?", 45)
                            .setSlots(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                            .setName(" ")
                                            .build(),
                                    0, 1, 2, 3, 4, 5, 6, 7, 8,
                                    9, 13, 17,
                                    18, 22, 26,
                                    27, 31, 35,
                                    36, 37, 38, 39, 40, 41, 42, 43, 44
                            ).setSlots(new Button(
                                               new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                                                       .setName("&c&lНЕТ")
                                                       .build()) {
                                           @Override
                                           public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                               menu.show(player, 0);
                                           }
                                       },
                                    10, 11, 12,
                                    19, 20, 21,
                                    28, 29, 30
                            ).setSlots(new Button(
                                               new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                                                       .setName("&a&lДА")
                                                       .build()) {
                                           @Override
                                           public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                               player.closeInventory();
                                               clear();
                                               TimerDatabase.remove(location);
                                           }
                                       },
                                    14, 15, 16,
                                    23, 24, 25,
                                    32, 33, 34
                            ).apply()
            );
            menu.show(event.getPlayer());
        }
    }

    private void onStop() {
        for (String string : timerData.getActions()) {
            String[] stringsElems = getAction(string);
            if (stringsElems == null) continue;
            String action = stringsElems[0];
            String value = stringsElems[1];

            long delay = 0;
            long period = 0;
            int repeats = 1;

            Matcher actionArgumentsMatcher = Pattern.compile("^(\\w+)\\((.*)\\)$").matcher(action);
            if (actionArgumentsMatcher.matches()) {
                action = actionArgumentsMatcher.group(1);
                String args = actionArgumentsMatcher.group(2);
                String[] arguments = args.split(";");
                for (String arg : arguments) {
                    Matcher argumentMatcher = Pattern.compile("^(\\w+)=(.*)$").matcher(arg);
                    if (argumentMatcher.matches()) {
                        String argName = argumentMatcher.group(1);
                        String argValue = argumentMatcher.group(2);
                        if (argName.equalsIgnoreCase("delay") || argName.equalsIgnoreCase("del") || argName.equalsIgnoreCase("d")) {
                            Matcher delayMatcher = Pattern.compile("^(\\d+)(\\w+)$").matcher(argValue);
                            if (delayMatcher.matches()) {
                                long delayValue = Long.parseLong(delayMatcher.group(1));
                                String delayMetric = delayMatcher.group(2);
                                if (delayMetric.equalsIgnoreCase("t") || delayMetric.equalsIgnoreCase("tick"))
                                    delay = delayValue;
                                else if (delayMetric.equalsIgnoreCase("s") || delayMetric.equalsIgnoreCase("sec"))
                                    delay = delayValue * 20;
                                else if (delayMetric.equalsIgnoreCase("m") || delayMetric.equalsIgnoreCase("min"))
                                    delay = delayValue * 20 * 60;
                                else
                                    Main.logger().error("Неверная единица измерения в '" + actionArgumentsMatcher.group(0) + "': '" + delayMetric + "'!");
                            } else {
                                Main.logger().error("Значение параметра '" + argName + "' имеет неверный формат: '" + argValue + "'!");
                            }
                        } else if (argName.equalsIgnoreCase("period") || argName.equalsIgnoreCase("per") || argName.equalsIgnoreCase("p")) {
                            Matcher periodMatcher = Pattern.compile("^(\\d+)(\\w+)$").matcher(argValue);
                            if (periodMatcher.matches()) {
                                long periodValue = Long.parseLong(periodMatcher.group(1));
                                String delayMetric = periodMatcher.group(2);
                                if (delayMetric.equalsIgnoreCase("t") || delayMetric.equalsIgnoreCase("tick"))
                                    period = periodValue;
                                else if (delayMetric.equalsIgnoreCase("s") || delayMetric.equalsIgnoreCase("sec"))
                                    period = periodValue * 20;
                                else if (delayMetric.equalsIgnoreCase("m") || delayMetric.equalsIgnoreCase("min"))
                                    period = periodValue * 20 * 60;
                                else
                                    Main.logger().error("Неверная единица измерения в '" + actionArgumentsMatcher.group(0) + "': '" + delayMetric + "'!");
                            } else {
                                Main.logger().error("Значение параметра '" + argName + "' имеет неверный формат: '" + argValue + "'!");
                            }
                        } else if (argName.equalsIgnoreCase("repeats") || argName.equalsIgnoreCase("rep") || argName.equalsIgnoreCase("r")) {
                            Matcher repeatMathcer = Pattern.compile("^(\\d+)$").matcher(argValue);
                            if (repeatMathcer.matches()) {
                                repeats = Integer.parseInt(repeatMathcer.group(1));
                            } else {
                                Main.logger().error("Значение параметра '" + argName + "' имеет неверный формат: '" + argValue + "'!");
                            }
                        } else {
                            Main.logger().error("Параметр '" + argName + "' не найден!");
                        }
                    } else {
                        Main.logger().error("Аргумент '" + arg + "' имеет неверный формат!");
                    }
                }
            } else if (!Pattern.compile("^(\\w+)$").matcher(action).matches()) {
                Main.logger().error("Неверное действие: '" + action + "'");
                continue;
            }

            switch (action) {
                case "command": {
                    ActionTask.create(
                            () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value), delay, period, repeats
                    );
                    break;
                } case "message": {
                    ActionTask.create(
                            () -> Bukkit.getOnlinePlayers().forEach(x -> x.sendMessage(ColorUtil.toColor(value))), delay, period, repeats
                    );
                    break;
                } default: {
                    File actionFile = new File("plugins/IncClocks/actions/" + action + ".js");
                    if (actionFile.exists()) {
                        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
                        ScriptEngine engine = factory.getScriptEngine();
                        String finalAction = action;
                        ActionTask.create(
                                () -> {
                                    try {
                                        Bindings bindings = engine.createBindings();
                                        bindings.put("arg", value);
                                        engine.eval(Files.newBufferedReader(actionFile.toPath(), StandardCharsets.UTF_8), bindings);
                                    } catch (ScriptException e) {
                                        Main.logger().error("[" + finalAction + "] При выполнении скрипта '" + finalAction + ".js' произошла ошибка!");
                                        Main.logger().error(e.getMessage());
                                    } catch (IOException e) {
                                        Main.logger().error("[" + finalAction + "] Файл '" + finalAction + ".js' не найден!");
                                    }
                                }, delay, period, repeats
                        );
                    } else {
                        Main.logger().error("[" + action + "] Файл '" + action + ".js' не найден!");
                    }
                    break;
                }
            }
        }
    }

    private String[] getAction(String string) {
        if (!string.isEmpty()) {
            if (string.contains(" ")) {
                String action = string.substring(0, string.indexOf(" "));
                if (action.length() > 2) {
                    if (action.startsWith("[") && action.endsWith("]")) {
                        action = action.substring(1, action.length() - 1);
                        String value = string.substring(string.indexOf(" ") + 1);
                        if (!value.isEmpty())
                            return new String[]{action, value};
                    }
                }
            } else {
                if (string.length() > 2) {
                    if (string.startsWith("[") && string.endsWith("]")) {
                        return new String[]{string.substring(1, string.length() - 1), null};
                    }
                }
            }
        }
        return null;
    }
}
