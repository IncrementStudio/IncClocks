package ru.incrementstudio.incclocks.timers;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.incrementstudio.incapi.Action;
import ru.incrementstudio.incclocks.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class ActionTask {
    private static final Map<Integer, BukkitTask> tasks = new HashMap<>();

    public static BukkitTask create(Action action, long delay, long period, int repeats) {
        if (repeats == 1 && delay == 0) {
            action.execute();
            return null;
        }
        int id;
        do {
            id = new Random().nextInt();
        } while (tasks.containsKey(id));
        int finalId = id;
        BukkitTask task = new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (i == repeats) {
                    tasks.remove(finalId);
                    cancel();
                    return;
                }
                action.execute();
                i++;
            }
        }.runTaskTimer(Main.getInstance(), delay, period);
        tasks.put(id, task);
        return task;
    }
}
