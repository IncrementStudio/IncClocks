package ru.incrementstudio.incclocks.timers;

import org.checkerframework.checker.regex.qual.Regex;
import ru.incrementstudio.incclocks.bases.BaseData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TimerData extends BaseData {
    private long time = 0;
    public long getTime() {
        return time;
    }
    private final List<String> actions;
    public List<String> getActions() {
        return actions;
    }

    public TimerData(String name, File configFile) {
        super(name, configFile);

        actions = config.get().contains("actions") ? config.get().getStringList("actions") : new ArrayList<>();
        switch (timeType) {
            case GAME:
                String timeMetricG = config.get().contains("time") ? config.get().getString("time") : "1h";
                List<String> timeMetricsG = Arrays.stream(timeMetricG.split("\\|")).collect(Collectors.toList());
                for (String timeMetricElementG : timeMetricsG) {
                    Matcher matcher = Pattern.compile("^(\\d+)([dhm])$").matcher(timeMetricElementG);
                    if (!matcher.matches()) continue;
                    long value = Long.parseLong(matcher.group(1));
                    String metric = matcher.group(2);
                    if (metric.equals("d")) time += value * 24000;
                    else if (metric.equals("h")) time += value * 1000;
                    else if (metric.equals("m")) time += (long) ((value * 1000) / 60.0);
                }
                break;
            case REAL:
                String timeMetricR = config.get().contains("time") ? config.get().getString("time") : "1m";
                List<String> timeMetricsR = Arrays.stream(timeMetricR.split("\\|")).collect(Collectors.toList());
                for (String timeMetricElementR : timeMetricsR) {
                    Matcher matcher = Pattern.compile("^(\\d+)([dhms])$").matcher(timeMetricElementR);
                    if (!matcher.matches()) continue;
                    long value = Long.parseLong(matcher.group(1));
                    String metric = matcher.group(2);
                    if (metric.equals("d")) time += value * 1000 * 60 * 60 * 24;
                    else if (metric.equals("h")) time += value * 1000 * 60 * 60;
                    else if (metric.equals("m")) time += value * 1000 * 60;
                    else if (metric.equals("s")) time += value * 1000;
                }
                break;
        }
    }
}
