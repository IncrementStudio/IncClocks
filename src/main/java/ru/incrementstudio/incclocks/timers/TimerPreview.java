package ru.incrementstudio.incclocks.timers;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ru.incrementstudio.incclocks.bases.BaseData;
import ru.incrementstudio.incclocks.bases.BasePreview;

public class TimerPreview extends BasePreview {
    @Override
    public void create(PlayerInteractEvent event, BaseData data, Location start, Vector u, Vector v, Vector d) {
        new Timer((TimerData) data, start, u, v, d);
        TimerDatabase.add(data.getName(), start, u, v, d);
    }
}
