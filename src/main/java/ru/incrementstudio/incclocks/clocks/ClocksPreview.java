package ru.incrementstudio.incclocks.clocks;

import org.bukkit.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ru.incrementstudio.incclocks.bases.BaseData;
import ru.incrementstudio.incclocks.bases.BasePreview;

public class ClocksPreview extends BasePreview {
    @Override
    public void create(PlayerInteractEvent event, BaseData data, Location start, Vector u, Vector v, Vector d) {
        new Clocks((ClocksData) data, start, u, v, d);
        ClocksDatabase.add(data.getName(), start, u, v, d);
    }
}
