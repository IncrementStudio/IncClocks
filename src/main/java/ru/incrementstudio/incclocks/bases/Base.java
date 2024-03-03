package ru.incrementstudio.incclocks.bases;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ru.incrementstudio.incclocks.BlockString;
import ru.incrementstudio.incclocks.Main;

import java.util.ArrayList;
import java.util.List;

public abstract class Base implements Listener {
    protected final BaseData data;
    protected final Location location;
    protected final World world;
    protected final BlockString timeString;
    protected Vector u, v, d;
    protected List<Block> blockList = new ArrayList<>();
    public List<Block> getBlockList() {
        return blockList;
    }

    public Base(BaseData data, Location location, Vector u, Vector v, Vector d) {
        this.data = data;
        this.location = location;
        world = location.getWorld();
        this.u = u;
        this.v = v;
        this.d = d;

        timeString = new BlockString(this, data.getFormatWithoutFormatting().length(), data.getGap(), getRelative(
                1 + data.getOffsetX() + data.getPaddingX(),
                1 + data.getOffsetY() + data.getPaddingY(),
                data.getWidth() - 1), u, v, data.getFont());

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());

        for (int U = 0; U < data.getPaddingX() * 2 + timeString.getWidth() + 2; U++) {
            for (int V = 0; V < data.getPaddingY() * 2 + data.getFont().getHeight() + 2; V++) {
                for (int D = 0; D < data.getWidth(); D++) {
                    Location blockLocation = getRelative(U, V, D);
                    Block block = world.getBlockAt(blockLocation);
                    if ((U >= data.getBorderRadius() || V >= data.getBorderRadius()) &&
                            (U >= data.getBorderRadius() || V <= data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1) &&
                            (U <= data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1 || V >= data.getBorderRadius()) &&
                            (U <= data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1 || V <= data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1)
                    ) {
                        if (U == 0 || U == data.getPaddingX() * 2 + timeString.getWidth() + 1 || V == 0 || V == data.getPaddingY() * 2 + data.getFont().getHeight() + 1 || D == 0) {
                            block.setType(data.getMaterialSet().getSides());
                            blockList.add(block);
                        } else if (D < data.getWidth() - 1) {
                            block.setType(data.getMaterialSet().getBack());
                            blockList.add(block);
                        }
                    } else if ((Math.pow(U - data.getBorderRadius(), 2) + Math.pow(V - data.getBorderRadius(), 2) < Math.pow(data.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - (data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1), 2) + Math.pow(V - data.getBorderRadius(), 2) < Math.pow(data.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - data.getBorderRadius(), 2) + Math.pow(V - (data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1), 2) < Math.pow(data.getBorderRadius() - 1, 2)) ||
                            (Math.pow(U - (data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1), 2) + Math.pow(V - (data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1), 2) < Math.pow(data.getBorderRadius() - 1, 2))) {
                        if (D == 0) {
                            block.setType(data.getMaterialSet().getSides());
                            blockList.add(block);
                        } else if (D < data.getWidth() - 1) {
                            block.setType(data.getMaterialSet().getBack());
                            blockList.add(block);
                        }
                    } else if ((Math.pow(U - data.getBorderRadius(), 2) + Math.pow(V - data.getBorderRadius(), 2) < Math.pow(data.getBorderRadius(), 2)) ||
                            (Math.pow(U - (data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1), 2) + Math.pow(V - data.getBorderRadius(), 2) < Math.pow(data.getBorderRadius(), 2)) ||
                            (Math.pow(U - data.getBorderRadius(), 2) + Math.pow(V - (data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1), 2) < Math.pow(data.getBorderRadius(), 2)) ||
                            (Math.pow(U - (data.getPaddingX() * 2 + timeString.getWidth() + 2 - data.getBorderRadius() - 1), 2) + Math.pow(V - (data.getPaddingY() * 2 + data.getFont().getHeight() + 2 - data.getBorderRadius() - 1), 2) < Math.pow(data.getBorderRadius(), 2))) {
                        block.setType(data.getMaterialSet().getSides());
                        blockList.add(block);
                    }
                }
            }
        }
    }

    private Location getRelative(int U, int V, int D) {
        return location.clone().add(
                U * u.getBlockX() + V * v.getBlockX() + D * d.getBlockX(),
                U * u.getBlockY() + V * v.getBlockY() + D * d.getBlockY(),
                U * u.getBlockZ() + V * v.getBlockZ() + D * d.getBlockZ()
        );
    }

    public void clear() {
        timeString.clear();
        for (Block block : blockList) {
            block.setType(Material.AIR);
        }
        blockList.clear();
    }

    public void onBreak(PlayerInteractEvent event) { }

    @EventHandler
    public void onDestroy(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (blockList.contains(event.getClickedBlock()) && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            onBreak(event);
        }
    }
}
