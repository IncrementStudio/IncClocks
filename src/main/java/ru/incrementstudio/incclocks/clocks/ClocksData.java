package ru.incrementstudio.incclocks.clocks;

import ru.incrementstudio.incclocks.bases.BaseData;

import java.io.File;

public class ClocksData extends BaseData {
    private final int timeZone;
    public int getTimeZone() {
        return timeZone;
    }

    public ClocksData(String name, File configFile) {
        super(name, configFile);
        timeZone = config.get().contains("time-zone") ? config.get().getInt("time-zone") : 0;
    }
}
