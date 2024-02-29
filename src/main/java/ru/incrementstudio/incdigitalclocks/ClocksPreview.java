package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Color;
import org.bukkit.Location;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClocksPreview implements Listener {
    private static Map<Player, Integer> players = new HashMap<>();

    public static void addPlayer(Player player, Config clocks) {
        players.put(player, 0);
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
                player.spawnParticle(Particle.REDSTONE, start, 0, 1, 1, 1, new Particle.DustOptions(
                        Color.BLUE, 1f
                ));
                player.spawnParticle(Particle.REDSTONE, start.add(u), 0, 1, 1, 1, new Particle.DustOptions(
                        Color.LIME, 1f
                ));
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
        double angle = players.get(player);
        BlockFace face = getBlockFace(player);
        if (face != null) {
            if (face == BlockFace.UP)
                return BlockFace.WEST.getDirection().rotateAroundY(Math.toRadians(angle));
            else if (face == BlockFace.DOWN)
                return BlockFace.EAST.getDirection().rotateAroundY(-Math.toRadians(angle));
            else if (face == BlockFace.NORTH)
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
        double angle = players.get(player);
        BlockFace face = getBlockFace(player);
        if (face != null) {
            if (face == BlockFace.UP)
                return BlockFace.SOUTH.getDirection().rotateAroundY(Math.toRadians(angle));
            else if (face == BlockFace.DOWN)
                return BlockFace.SOUTH.getDirection().rotateAroundY(-Math.toRadians(angle));
            else if (face == BlockFace.NORTH || face == BlockFace.SOUTH || face == BlockFace.EAST || face == BlockFace.WEST)
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

            } else {
                players.put(event.getPlayer(), players.get(event.getPlayer()) + 90);
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
