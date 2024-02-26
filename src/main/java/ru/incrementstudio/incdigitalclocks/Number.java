package ru.incrementstudio.incdigitalclocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class Number {
    private int length, gap;
    private final DigitSet digitSet;
    private final MaterialSet materialSet;
    private final Location location;
    private final World world;
    private final BlockFace facing;
    private final Digit[] digits;

    public Number(int length, int gap, Location location, BlockFace facing, DigitSet digitSet, MaterialSet materialSet) {
        this.length = length;
        this.gap = gap;
        this.location = location;
        world = location.getWorld();
        this.facing = facing;
        this.digitSet = digitSet;
        this.materialSet = materialSet;
        digits = new Digit[length];
        for (int i = 0; i < digits.length; i++) {
            if (facing == BlockFace.UP)
                digits[i] = new Digit(location.clone().add(i * (digitSet.getWidth() + gap), 0, 0), facing, digitSet, materialSet);
            if (facing == BlockFace.DOWN)
                digits[i] = new Digit(location.clone().subtract(i * (digitSet.getWidth() + gap), 0, 0), facing, digitSet, materialSet);
        }
    }

    public void setValue(long value) {
        String string = String.valueOf(value);
        if (string.length() < length)
            string = "0".repeat(length - string.length()) + string;
        for (int i = 0; i < digits.length; i++) {
            digits[i].setValue(Integer.parseInt(string.charAt(i) + ""));
        }
    }
}
