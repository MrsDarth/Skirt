package io.github.mrsdarth.skirt.elements.direction;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Direction;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SkirtDirection extends Direction {

    static {
        Classes.registerClass(new ClassInfo<>(SkirtDirection.class, "skirtdirection")
                .parser(new Parser<>() {
                    @Override
                    public @Nullable SkirtDirection parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(SkirtDirection dir, int flags) {
                        return dir.toString();
                    }

                    @Override
                    public @NotNull String toVariableNameString(SkirtDirection dir) {
                        return StringUtils.joinWith(";", "skirt direction", dir.hasLength, dir.length, dir.toward, dir.target);
                    }

                    @Override
                    public @NotNull String getVariableNamePattern() {
                        return "skirt direction;.+";
                    }
                }));
    }

    private final Object target;
    private final boolean toward, hasLength;
    private final double length;

    public static SkirtDirection newDirection(@Nullable Object target, boolean toward, @Nullable Double length) {
        return target instanceof Entity || target instanceof Location || target instanceof Vector ? new SkirtDirection(target, toward, length) : null;
    }

    private SkirtDirection(Object target, boolean toward, Double length) {
        this.target = target instanceof Location loc ? loc.toVector() : target;
        this.toward = toward;
        this.hasLength = length != null;
        this.length = this.hasLength ? length : 0;
    }

    public Vector getTarget() {
        return target instanceof Entity entity ? entity.getLocation().toVector() : target instanceof Vector vector ? vector : null;
    }

    private Vector direction(Location from) {
        Vector direction = getTarget().subtract(from.toVector());
        if (!toward) direction.multiply(-1);
        if (hasLength) direction.normalize().multiply(length);
        try {
            direction.checkFinite();
            return direction;
        } catch (Exception ex) {
            return new Vector();
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(CollectionUtils.array(target, toward, length, hasLength));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof SkirtDirection dir) && (this.toward == dir.toward) && (this.hasLength == dir.hasLength) && (this.length == dir.length) && (this.getTarget() == dir.getTarget());
    }

    @Override
    public @NotNull String toString() {
        return (this.hasLength ? this.length + " " : "") + (this.toward ? "toward " : "away from ") + Classes.toString(this.target);
    }

    @Override
    public boolean isRelative() {
        return true;
    }

    @Override
    public @NotNull Vector getDirection(@NotNull Location loc) {
        return direction(loc);
    }

    @Override
    public @NotNull Vector getDirection(Block block) {
        return getDirection(block.getLocation());
    }

}
