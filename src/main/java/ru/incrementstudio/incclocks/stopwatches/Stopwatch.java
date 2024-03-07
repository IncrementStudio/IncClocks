package ru.incrementstudio.incclocks.stopwatches;

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

public class Stopwatch extends Base {
    private final StopwatchData stopwatchData;
    private BukkitTask updateTimeTask;
    private boolean isWorking = false;
    private long startTime;

    public Stopwatch(StopwatchData data, Location location, Vector u, Vector v, Vector d) {
        super(data, location, u, v, d);
        stopwatchData = data;

        switch (data.getTimeType()) {
            case REAL:
                timeString.setValue(
                        stopwatchData.getFormat()
                                .replace("%d", "00")
                                .replace("%h", "00")
                                .replace("%m", "00")
                                .replace("%s", "00")
                );
                break;
            case GAME:
                timeString.setValue(
                        stopwatchData.getFormat()
                                .replace("%d", "00")
                                .replace("%h", "00")
                                .replace("%m", "00")
                );
                break;
        }

        Main.getInstance().getStopwatches().add(this);
    }

    public void clear() {
        if (updateTimeTask != null)
            updateTimeTask.cancel();
        super.clear();
    }

    @Override
    public void onBreak(PlayerInteractEvent event) {
        Menu menu = new Menu();
        menu.addPage(
                new Page("Секундомер", 45)
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
                                           updateTimeTask = new BukkitRunnable() {
                                               @Override
                                               public void run() {
                                                   switch (stopwatchData.getTimeType()) {
                                                       case GAME:
                                                           long time = (world.getGameTime() - startTime) % 24000;
                                                           long daysG = (world.getGameTime() - startTime) / 24000;
                                                           long hoursG = time / 1000;
                                                           long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, time % 1000));
                                                           timeString.setValue(
                                                                   stopwatchData.getFormat()
                                                                           .replace("%d", "0".repeat(2 - String.valueOf(daysG).length()) + daysG)
                                                                           .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                                                           .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                                                           );
                                                           break;
                                                       case REAL:
                                                           long daysR = ((System.currentTimeMillis() - startTime) / (1000 * 60 * 60 * 24)) % 99;
                                                           long hoursR = ((System.currentTimeMillis() - startTime) / (1000 * 60 * 60)) % 24;
                                                           long minutesR = ((System.currentTimeMillis() - startTime) / (1000 * 60)) % 60;
                                                           long secondsR = ((System.currentTimeMillis() - startTime) / 1000) % 60;
                                                           timeString.setValue(
                                                                   stopwatchData.getFormat()
                                                                           .replace("%d", "0".repeat(2 - String.valueOf(daysR).length()) + daysR)
                                                                           .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                                                           .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                                                           .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                                                           );
                                                           break;
                                                   }
                                               }
                                           }.runTaskTimer(Main.getInstance(), 0L, 1L);
                                           switch (data.getTimeType()) {
                                               case REAL:
                                                   startTime = System.currentTimeMillis();
                                                   break;
                                               case GAME:
                                                   startTime = world.getGameTime();
                                                   break;
                                           }
                                           player.closeInventory();
                                       }
                                   },
                                14, 15, 16,
                                23, 24, 25,
                                32, 33, 34
                        ).setSlots(new Button(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                                           .setName("&c&lСТОП")
                                           .build()) {
                                       @Override
                                       public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                           if (!isWorking) return;
                                           isWorking = false;
                                           updateTimeTask.cancel();
                                           switch (data.getTimeType()) {
                                               case REAL:
                                                   timeString.setValue(
                                                           stopwatchData.getFormat()
                                                                   .replace("%d", "00")
                                                                   .replace("%h", "00")
                                                                   .replace("%m", "00")
                                                                   .replace("%s", "00")
                                                   );
                                                   break;
                                               case GAME:
                                                   timeString.setValue(
                                                           stopwatchData.getFormat()
                                                                   .replace("%d", "00")
                                                                   .replace("%h", "00")
                                                                   .replace("%m", "00")
                                                   );
                                                   break;
                                           }
                                           player.closeInventory();
                                       }
                                   },
                                10, 11, 12,
                                19, 20, 21,
                                28, 29, 30
                        ).setSlot(new Button(new ItemBuilder(Material.RED_WOOL)
                                          .setName("&c&lУБРАТЬ")
                                          .build()) {
                                      @Override
                                      public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                          menu.show(player, 1);
                                      }
                                  }, 8
                        ).apply()
        ).addPage(
                new Page("Вы хотите убрать секундомер?", 45)
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
                                           StopwatchDatabase.remove(location);
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
