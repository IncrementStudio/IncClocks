package ru.incrementstudio.incclocks.bases;

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
import ru.incrementstudio.incapi.utils.ColorUtil;
import ru.incrementstudio.incclocks.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasePreview implements Listener {
    private final int distance = 100;
    private Map<Player, Map.Entry<BaseData, Integer>> players = new HashMap<>();

    public void addPlayer(BaseData data, Player player) {
        if (players.containsKey(player)) {
            player.sendMessage(ColorUtil.toColor("&9[&bIncClocks&9] &cСейчас вы не можете этого сделать!"));
            return;
        }
        players.put(player, Map.entry(data, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!players.containsKey(player)) {
                    cancel();
                    return;
                }
                Block target = player.getTargetBlock(null, distance);
                Vector u = u(player);
                Vector v = v(player);
                Vector d = d(player);
                if (d == null || u == null || v == null) return;
                Location start = target.getLocation().add(d);
                boolean empty = true;
                for (int U = 0; U < data.getPaddingX() * 2 + data.getUWidth() + 2; U++) {
                    for (int V = 0; V < data.getPaddingY() * 2 + data.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < data.getWidth(); D++) {
                            if (start.clone().add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                    U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                    U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                            ).getBlock().getType() != Material.AIR)
                                empty = false;
                        }
                    }
                }
                for (int U = 0; U < data.getPaddingX() * 2 + data.getUWidth() + 2; U++) {
                    for (int V = 0; V < data.getPaddingY() * 2 + data.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < data.getWidth(); D++) {
                            if (((U == 0 || U == data.getPaddingX() * 2 + data.getUWidth() + 1) && ((V == 0 || V == data.getPaddingY() * 2 + data.getFont().getHeight() + 1) || (D == 0 || D == data.getWidth() - 1))) ||
                                    ((V == 0 || V == data.getPaddingY() * 2 + data.getFont().getHeight() + 1) && ((U == 0 || U == data.getPaddingX() * 2 + data.getUWidth() + 1) || (D == 0 || D == data.getWidth() - 1))) ||
                                    ((D == 0 || D == data.getWidth() - 1) && ((V == 0 || V == data.getPaddingY() * 2 + data.getFont().getHeight() + 1) || (U == 0 || U == data.getPaddingX() * 2 + data.getUWidth() + 1)))) {
                                player.spawnParticle(Particle.REDSTONE, start.clone()
                                        .add(0.5, 0.5, 0.5)
                                        .add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                                U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                                U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                                        ), 0, 1, 1, 1, new Particle.DustOptions(empty ? Color.LIME : Color.RED, 1f)
                                );
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }

    public BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, distance);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isSolid()) return null;
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    private Vector d(Player player) {
        BlockFace face = getBlockFace(player);
        if (face != null)
            return face.getDirection();
        return null;
    }

    private Vector u(Player player) {
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

    private Vector v(Player player) {
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
                Block target = event.getPlayer().getTargetBlock(null, distance);
                Vector u = u(event.getPlayer());
                Vector v = v(event.getPlayer());
                Vector d = d(event.getPlayer());
                if (d == null || u == null || v == null) return;
                Location start = target.getLocation().add(d);

                BaseData data = players.get(event.getPlayer()).getKey();

                boolean empty = true;
                for (int U = 0; U < data.getPaddingX() * 2 + data.getUWidth() + 2; U++) {
                    for (int V = 0; V < data.getPaddingY() * 2 + data.getFont().getHeight() + 2; V++) {
                        for (int D = 0; D < data.getWidth(); D++) {
                            if (start.clone().add(U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                                    U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                                    U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
                            ).getBlock().getType() != Material.AIR)
                                empty = false;
                        }
                    }
                }
                if (empty) {
                    create(event, data, start, u, v, d);
                    players.remove(event.getPlayer());
                }
            } else if (d(event.getPlayer()) == null) {
                players.remove(event.getPlayer());
            } else {
                players.put(event.getPlayer(), Map.entry(players.get(event.getPlayer()).getKey(), (players.get(event.getPlayer()).getValue() + 90) % 360));
            }
        }
        event.setCancelled(true);
    }

    public abstract void create(PlayerInteractEvent event, BaseData data, Location start, Vector u, Vector v, Vector d);

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!players.containsKey(event.getPlayer())) return;
        players.remove(event.getPlayer());
    }
}
