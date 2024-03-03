package ru.incrementstudio.incclocks;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Clocks implements Listener {
    private final ClocksData clocksData;
    private final Location location;
    private final World world;
    private final BlockString timeString;
    private Vector u, v, d;
    private List<Block> blockList = new ArrayList<>();

    public List<Block> getBlockList() {
        return blockList;
    }

    private BukkitTask updateTimeTask;

    public Clocks(String name, Location location, Vector u, Vector v, Vector d) {
        clocksData = new ClocksData(name);
        this.location = location;
        world = location.getWorld();
        this.u = u;
        this.v = v;
        this.d = d;

        timeString = new BlockString(this, clocksData.getFormatWithoutFormatting().length(), clocksData.getGap(), getRelative(
                1 + clocksData.getOffsetX() + clocksData.getPaddingX(),
                1 + clocksData.getOffsetY() + clocksData.getPaddingY(),
                clocksData.getWidth() - 1), u, v, clocksData.getFont());

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        updateTimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                switch (clocksData.getTimeType()) {
                    case GAME:
                        long hoursG = Bukkit.getWorld("world").getTime() / 1000 +
                                (Bukkit.getWorld("world").getTime() / 1000 + 6 < 24 ? 6 : -18);
                        long minutesG = (long) MathUtil.lerp(0, 60, MathUtil.inverseLerp(0, 1000, Bukkit.getWorld("world").getTime() % 1000));
                        timeString.setValue(
                                clocksData.getFormat()
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursG).length()) + hoursG)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesG).length()) + minutesG)
                        );
                        break;
                    case REAL:
                        long hoursR = (new Date(System.currentTimeMillis()).getHours() + clocksData.getTimeZone()) % 24;
                        long minutesR = new Date(System.currentTimeMillis()).getMinutes();
                        long secondsR = new Date(System.currentTimeMillis()).getSeconds();
                        timeString.setValue(
                                clocksData.getFormat()
                                        .replace("%h", "0".repeat(2 - String.valueOf(hoursR).length()) + hoursR)
                                        .replace("%m", "0".repeat(2 - String.valueOf(minutesR).length()) + minutesR)
                                        .replace("%s", "0".repeat(2 - String.valueOf(secondsR).length()) + secondsR)
                        );
                        break;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);

        for (int U = 0; U < clocksData.getPaddingX() * 2 + timeString.getWidth() + 2; U++) {
            for (int V = 0; V < clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2; V++) {
                for (int D = 0; D < clocksData.getWidth(); D++) {
                    Location blockLocation = getRelative(U, V, D);
                    Block block = world.getBlockAt(blockLocation);
                    if ((U >= clocksData.getBorderRadius() || V >= clocksData.getBorderRadius()) &&
                            (U >= clocksData.getBorderRadius() || V <= clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1) &&
                            (U <= clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1 || V >= clocksData.getBorderRadius()) &&
                            (U <= clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1 || V <= clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1)
                    ) {
                        if (U == 0 || U == clocksData.getPaddingX() * 2 + timeString.getWidth() + 1 || V == 0 || V == clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 1 || D == 0) {
                            block.setType(clocksData.getMaterialSet().getSides());
                            blockList.add(block);
                        } else if (D < clocksData.getWidth() - 1) {
                            block.setType(clocksData.getMaterialSet().getBack());
                            blockList.add(block);
                        }
                    } else if ((Math.pow(U - clocksData.getBorderRadius(), 2) + Math.pow(V - clocksData.getBorderRadius(), 2) < Math.pow(clocksData.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - (clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1), 2) + Math.pow(V - clocksData.getBorderRadius(), 2) < Math.pow(clocksData.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - clocksData.getBorderRadius(), 2) + Math.pow(V - (clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1), 2) < Math.pow(clocksData.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - (clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1), 2) + Math.pow(V - (clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1), 2) < Math.pow(clocksData.getBorderRadius() - 1, 2))) {
                        if (D == 0) {
                            block.setType(clocksData.getMaterialSet().getSides());
                            blockList.add(block);
                        } else if (D < clocksData.getWidth() - 1) {
                            block.setType(clocksData.getMaterialSet().getBack());
                            blockList.add(block);
                        }
                    } else if ((Math.pow(U - clocksData.getBorderRadius(), 2) + Math.pow(V - clocksData.getBorderRadius(), 2) < Math.pow(clocksData.getBorderRadius(), 2)) ||
                            (Math.pow(U - (clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1), 2) + Math.pow(V - clocksData.getBorderRadius(), 2) < Math.pow(clocksData.getBorderRadius(), 2)) ||
                            (Math.pow(U - clocksData.getBorderRadius(), 2) + Math.pow(V - (clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1), 2) < Math.pow(clocksData.getBorderRadius(), 2)) ||
                            (Math.pow(U - (clocksData.getPaddingX() * 2 + timeString.getWidth() + 2 - clocksData.getBorderRadius() - 1), 2) + Math.pow(V - (clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2 - clocksData.getBorderRadius() - 1), 2) < Math.pow(clocksData.getBorderRadius(), 2))) {
                        block.setType(clocksData.getMaterialSet().getSides());
                        blockList.add(block);
                    }
                }
            }
        }

        Main.getInstance().getClocks().add(this);
    }

    private Location getRelative(int U, int V, int D) {
        return location.clone().add(
                U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
        );
    }

    public void clear() {
        updateTimeTask.cancel();
        timeString.clear();
        for (Block block : blockList) {
            block.setType(Material.AIR);
        }
        blockList.clear();
    }

    public void onBreak(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("clocks.admin")) {
            Menu menu = new Menu()
                    .addPage(
                            new Page("Вы хотите убрать часы?", 45)
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
                                                               .setName("&cНЕТ")
                                                               .build()) {
                                                   @Override
                                                   public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                                       player.closeInventory();
                                                   }
                                               },
                                            10, 11, 12,
                                            19, 20, 21,
                                            28, 29, 30
                                    ).setSlots(new Button(
                                                       new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                                                               .setName("&aДА")
                                                               .build()) {
                                                   @Override
                                                   public void onClick(Player player, InventoryClickEvent inventoryClickEvent) {
                                                       player.closeInventory();
                                                       clear();
                                                       Database.removeClocks(location);
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

    @EventHandler
    public void onDestroy(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (blockList.contains(event.getClickedBlock()) && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            onBreak(event);
        }
    }
}
