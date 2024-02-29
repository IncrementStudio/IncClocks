package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.incrementstudio.incapi.configs.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClocksPreview implements Listener {
    private static Map<Player, Map.Entry<ClocksData, Integer>> players = new HashMap<>();

    public static void addPlayer(Player player, String name) {
        ClocksData clocksData = new ClocksData(name);
        players.put(player, Map.entry(clocksData, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!players.containsKey(player)) {
                    cancel();
                    return;
                }
                Block target = player.getTargetBlock(null, 25);
                Vector u = u(player);
                Vector v = v(player);
                Vector d = d(player);
                if (d == null || u == null || v == null) return;
                Location start = target.getLocation().add(d);
                boolean empty = true;
                for (int U = 0; U < clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 2; U++) {
                    for (int V = 0; V < clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < clocksData.getWidth(); D++) {
                            if (start.clone().add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                    U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                    U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                            ).getBlock().getType() != Material.AIR)
                                empty = false;
                        }
                    }
                }
                for (int U = 0; U < clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 2; U++) {
                    for (int V = 0; V < clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < clocksData.getWidth(); D++) {
                            if (((U == 0 || U == clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 1) && ((V == 0 || V == clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 1) || (D == 0 || D == clocksData.getWidth() - 1))) ||
                                    ((V == 0 || V == clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 1) && ((U == 0 || U == clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 1) || (D == 0 || D == clocksData.getWidth() - 1))) ||
                                    ((D == 0 || D == clocksData.getWidth() - 1) && ((V == 0 || V == clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 1) || (U == 0 || U == clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 1)))) {
                                player.spawnParticle(Particle.REDSTONE, start.clone()
                                        .add(0.5, 0.5, 0.5)
                                        .add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                                U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                                U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                                        ), 0, 1, 1, 1, new Particle.DustOptions(empty ? Color.BLUE : Color.RED, 2f)
                                );
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public static BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 25);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    private static Vector d(Player player) {
        BlockFace face = getBlockFace(player);
        if (face != null)
            return face.getDirection();
        return null;
    }

    private static Vector u(Player player) {
        double angle = players.get(player).getValue();
        BlockFace face = getBlockFace(player);
        if (face != null) {
            if (face == BlockFace.UP) {
                Vector result = BlockFace.WEST.getDirection().rotateAroundY(Math.toRadians(angle));
                return new Vector((int) result.getX(), (int) result.getY(), (int) result.getZ());
            } else if (face == BlockFace.DOWN) {
                Vector result = BlockFace.EAST.getDirection().rotateAroundY(-Math.toRadians(angle));
                return new Vector((int) result.getX(), (int) result.getY(), (int) result.getZ());
            } else if (face == BlockFace.NORTH)
                return BlockFace.WEST.getDirection();
            else if (face == BlockFace.SOUTH)
                return BlockFace.EAST.getDirection();
            else if (face == BlockFace.EAST)
                return BlockFace.NORTH.getDirection();
            else if (face == BlockFace.WEST)
                return BlockFace.SOUTH.getDirection();
        }
        return null;
    }

    private static Vector v(Player player) {
        double angle = players.get(player).getValue();
        BlockFace face = getBlockFace(player);
        if (face != null) {
            if (face == BlockFace.UP) {
                Vector result = BlockFace.SOUTH.getDirection().rotateAroundY(Math.toRadians(angle));
                return new Vector((int) result.getX(), (int) result.getY(), (int) result.getZ());
            } else if (face == BlockFace.DOWN) {
                Vector result = BlockFace.SOUTH.getDirection().rotateAroundY(-Math.toRadians(angle));
                return new Vector((int) result.getX(), (int) result.getY(), (int) result.getZ());
            } else if (face == BlockFace.NORTH || face == BlockFace.SOUTH || face == BlockFace.EAST || face == BlockFace.WEST)
                return BlockFace.UP.getDirection();
        }
        return null;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getPlayer().isSneaking()) {
                Block target = event.getPlayer().getTargetBlock(null, 25);
                Vector u = u(event.getPlayer());
                Vector v = v(event.getPlayer());
                Vector d = d(event.getPlayer());
                if (d == null || u == null || v == null) return;
                Location start = target.getLocation().add(d);

                ClocksData clocksData = players.get(event.getPlayer()).getKey();

                boolean empty = true;
                for (int U = 0; U < clocksData.getPaddingX() * 2 + clocksData.getuWidth() + 2; U++) {
                    for (int V = 0; V < clocksData.getPaddingY() * 2 + clocksData.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < clocksData.getWidth(); D++) {
                            if (start.clone().add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                    U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                    U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                            ).getBlock().getType() != Material.AIR)
                                empty = false;
                        }
                    }
                }
                if (empty) {
                    Clocks clocks = new Clocks(clocksData.getName(), start, u, v, d);
                    Main.getInstance().getClocks().add(clocks);
                    players.remove(event.getPlayer());
                }
            } else {
                players.put(event.getPlayer(), Map.entry(players.get(event.getPlayer()).getKey(), (players.get(event.getPlayer()).getValue() + 90) % 360));
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        players.remove(event.getPlayer());
    }
}
