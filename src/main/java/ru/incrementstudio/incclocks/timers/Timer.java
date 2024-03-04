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
import ru.incrementstudio.incapi.menu.Button;
import ru.incrementstudio.incapi.menu.Menu;
import ru.incrementstudio.incapi.menu.Page;
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incapi.utils.MathUtil;
import ru.incrementstudio.incapi.utils.builders.ItemBuilder;
import ru.incrementstudio.incclocks.Main;
import ru.incrementstudio.incclocks.bases.Base;
import ru.incrementstudio.incclocks.bases.BaseData;

import java.util.Date;
import java.util.List;

public class Timer extends Base {
    private final TimerData timerData;
    private BukkitTask updateTimeTask;
    private boolean isWorking = false;
    private long startTime;
    private long timerTime;
    private final Menu menu = new Menu();
    private final Menu settingsMenu = new Menu() {
        @Override
        public void onPlayerClose(Player player, InventoryCloseEvent event) {
            menu.show(player);
        }
    };

    public Timer(TimerData data, Location location, Vector u, Vector v, Vector d) {
        super(data, location, u, v, d);
        timerData = data;

        switch (timerData.getTimeType()) {
            case GAME:
                timerTime = 1000;
                break;
            case REAL:
                timerTime = 1000 * 60;
                break;
        }
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
        reloadSettingsMenu();
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
                                                           cancel();
                                                           return;
                                                       }
                                                       switch (timerData.getTimeType()) {
                                                           case GAME:
                                                               long currentTimeG = (long) MathUtil.lerp(timerTime, 0, MathUtil.inverseLerp(startTime, startTime + timerTime, startTime + timerTime - world.getGameTime()));
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
                                                               long currentTimeR = (long) MathUtil.lerp(timerTime, 0, MathUtil.inverseLerp(startTime, startTime + timerTime, startTime + timerTime - System.currentTimeMillis()));
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
                            ).setSlot(new Button(new ItemBuilder(Material.COMMAND_BLOCK)
                                              .setName("&b&lНАСТРОЙКА")
                                              .build()) {
                                          @Override
                                          public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                              settingsMenu.show(player);
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
            );
            menu.show(event.getPlayer());
        }
    }

    private void reloadSettingsMenu() {
        settingsMenu.clearPages();
        settingsMenu.addPage(new Page("Настройка таймера", 45)
                .setSlots(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                .setName(" ")
                                .build(),
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9, 17,
                        18, 26,
                        27, 35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44
                ).setSlot(new Button(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                  .setName("&a&lДНИ")
                                  .setLore(List.of(
                                          ColorUtil.toColor("&fТекущее значение&7: &e") + (timerTime / (1000 * 60 * 60 * 24)),
                                          "",
                                          "&bЛКМ &7- &f+1",
                                          "&bПКМ &7- &f-1",
                                          "&bSHIFT + ЛКМ &7- &f+10",
                                          "&bSHIFT + ПКМ &7- &f-10"
                                  ))
                                  .build()) {
                              @Override
                              public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                  switch (timerData.getTimeType()) {
                                      case GAME:
                                          if (inventoryClickEvent.isLeftClick()) {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime + 24000, 24000 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime + 24000 * 10, 24000 * 99), 0);
                                              }
                                          } else {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime - 24000, 24000 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime - 24000 * 10, 24000 * 99), 0);
                                              }
                                          }
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
                                          if (inventoryClickEvent.isLeftClick()) {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime + 1000 * 60 * 60 * 24, 1000L * 60 * 60 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime + 1000 * 60 * 60 * 24 * 10, 1000L * 60 * 60 * 24 * 99), 0);
                                              }
                                          } else {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime - 1000 * 60 * 60 * 24, 1000L * 60 * 60 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime - 1000 * 60 * 60 * 24 * 10, 1000L * 60 * 60 * 24 * 99), 0);
                                              }
                                          }
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
                                  reloadSettingsMenu();
                                  settingsMenu.show(player);
                              }
                          }, 20
                ).setSlot(new Button(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                                  .setName("&a&lЧАСЫ")
                                  .setLore(List.of(
                                          ColorUtil.toColor("&fТекущее значение&7: &e") + (timerData.getTimeType() == BaseData.TimeType.REAL ? (timerTime / (1000 * 60 * 60)) % 24 : (timerTime % 24000) / 1000),
                                          "",
                                          "&bЛКМ &7- &f+1",
                                          "&bПКМ &7- &f-1",
                                          "&bSHIFT + ЛКМ &7- &f+10",
                                          "&bSHIFT + ПКМ &7- &f-10"
                                  ))
                                  .build()) {
                              @Override
                              public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                  switch (timerData.getTimeType()) {
                                      case GAME:
                                          if (inventoryClickEvent.isLeftClick()) {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime + 1000, 1000 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime + 1000 * 10, 1000 * 24 * 99), 0);
                                              }
                                          } else {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime - 1000, 1000 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime - 1000 * 10, 1000 * 24 * 99), 0);
                                              }
                                          }
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
                                          if (inventoryClickEvent.isLeftClick()) {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime + 1000 * 60 * 60, 1000L * 60 * 60 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime + 1000 * 60 * 60 * 10, 1000L * 60 * 60 * 24 * 99), 0);
                                              }
                                          } else {
                                              if (!inventoryClickEvent.isShiftClick()) {
                                                  timerTime = Math.max(Math.min(timerTime - 1000 * 60 * 60, 1000L * 60 * 60 * 24 * 99), 0);
                                              } else {
                                                  timerTime = Math.max(Math.min(timerTime - 1000 * 60 * 60 * 10, 1000L * 60 * 60 * 24 * 99), 0);
                                              }
                                          }
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
                                  reloadSettingsMenu();
                                  settingsMenu.show(player);
                              }
                          }, 22
                ).apply()
        );
    }
}
