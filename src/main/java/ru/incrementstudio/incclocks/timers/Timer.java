package ru.incrementstudio.incclocks.timers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.incrementstudio.incapi.menu.Button;
import ru.incrementstudio.incapi.menu.Menu;
import ru.incrementstudio.incapi.menu.Page;
import ru.incrementstudio.incapi.utils.MathUtil;
import ru.incrementstudio.incapi.utils.builders.ItemBuilder;
import ru.incrementstudio.incclocks.Main;
import ru.incrementstudio.incclocks.bases.Base;

import java.util.Date;

public class Timer extends Base {
    private final TimerData timerData;
    private BukkitTask updateTimeTask;
    private long currentTime;

    public Timer(TimerData data, Location location, Vector u, Vector v, Vector d) {
        super(data, location, u, v, d);
        timerData = data;

        updateTimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                switch (timerData.getTimeType()) {
                    case GAME:
                        long hoursG = world.getTime() / 1000 +
                                (world.getTime() / 1000 + 6 < 24 ? 6 : -18);
                        long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, world.getTime() % 1000));
                        timeString.setValue(
                                timerData.getFormat()
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                        );
                        break;
                    case REAL:
                        long hoursR = new Date(System.currentTimeMillis()).getHours();
                        long minutesR = new Date(System.currentTimeMillis()).getMinutes();
                        long secondsR = new Date(System.currentTimeMillis()).getSeconds();
                        timeString.setValue(
                                timerData.getFormat()
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                        .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                        );
                        break;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);

        Main.getInstance().getTimers().add(this);
    }

    public void clear() {
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
                                    18, 26,
                                    27, 31, 35,
                                    36, 37, 38, 39, 40, 41, 42, 43, 44
                            ).setSlots(new Button(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                                              .setName("&a&lСТАРТ")
                                              .build()) {
                                          @Override
                                          public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
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
                                               player.closeInventory();
                                           }
                                       },
                                    10, 11, 12,
                                    19, 20, 21,
                                    28, 29, 30
                            ).setSlot(new Button(new ItemBuilder(Material.COMMAND_BLOCK)
                                              .setName("&b&lНАСТРОЙКА")
                                              .build()) {
                                          @Override
                                          public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                              menu.show(player, 2);
                                          }
                                      }, 22
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
            ).addPage(
                    new Page("Настройка таймера", 45)
                            .setSlots(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                            .setName(" ")
                                            .build(),
                                    0, 1, 2, 3, 4, 5, 6, 7, 8,
                                    9, 17,
                                    18, 26,
                                    27, 35,
                                    36, 37, 38, 39, 40, 41, 42, 43, 44
                            ).apply()
            );
//            menu.setPage(2, menu.getPage(2));
            menu.show(event.getPlayer());
        }
    }
}
