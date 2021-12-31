package io.github.mrsdarth.skirt.elements.raytrace;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.direction.DirectionUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Predicate;

@Name("Ray Trace")
@Description("Starts a ray trace")
@Examples({})
@Since("1.0.0")

public class ExprNewRayTrace extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprNewRayTrace.class, RayTraceResult.class, ExpressionType.COMBINED,
                "[block] ray[ ]trace[s] (of|from) %livingentities% [using [fluid[collision][mode]] %-fluidcollisionmode%][[,] [with] max %-number%]",
                "block ray[ ]trace[ starting] from %location% using %direction%[[,] [fluid[collision][mode]] %-fluidcollisionmode%][[,] (1¦(to ignore|ignoring) passable[ blocks])][[,] [with] max %-number%]",
                "ray[ ]trace [searching] for [this specific] %block% [starting] from %location% using %direction%[[,] [fluid[collision][mode]] %-fluidcollisionmode%][[,] [with] max %-number%]",
                "ray[ ]trace [searching] for [this specific] %boundingbox% [starting] from %vector/location% using %direction%[[,] [with] max %-number%]",
                "entity ray[ ]trace [starting] from %location% using %direction%[[,] [with] max %-number%][[,] ray[ ]size %-number%][[,] filter[ing] [out] %-entities%]",
                "[generic] ray[ ]trace [starting] from %location% using %direction%[[,] [with] max %number%][[,] [fluid[collision][mode]] %-fluidcollisionmode%][[,] (1¦(to ignore|ignoring) passable[ blocks])][[, ] ray[ ]size %number%][[, ] filter[ing] [out] %-entities%]");
    }

    private Expression<LivingEntity> livingEntityExpr;
    private Expression<FluidCollisionMode> collisionModeExpr;
    private Expression<Number> numberExpr1, numberExpr2;
    private Expression<Location> locationExpr;
    private Expression<Direction> directionExpr;
    private Expression<Block> blockExpr;
    private Expression<BoundingBox> boundingBoxExpr;
    private Expression<?> positionExpr;
    private Expression<Entity> entityExpr;

    private boolean ignore;
    private int pattern;

    @Override
    protected @Nullable
    RayTraceResult[] get(@NotNull Event e) {
        Number max = numberExpr1 == null ? SkriptConfig.maxTargetBlockDistance.value() : numberExpr1.getSingle(e);
        Number raySize = numberExpr2 == null ? 0.0D : numberExpr2.getSingle(e);
        FluidCollisionMode mode = collisionModeExpr == null ? FluidCollisionMode.ALWAYS : collisionModeExpr.getSingle(e);

        if (max == null || mode == null || raySize == null) return null;

        Location loc = locationExpr == null ? null : locationExpr.getSingle(e);
        Direction dir = directionExpr == null ? null : directionExpr.getSingle(e);
        Block block = blockExpr == null ? null : blockExpr.getSingle(e);
        BoundingBox box = boundingBoxExpr == null ? null : boundingBoxExpr.getSingle(e);
        Object position = positionExpr == null ? null : positionExpr.getSingle(e);

        Predicate<Entity> filter;
        if (entityExpr == null)
            filter = null;
        else {
            Entity[] entities = entityExpr.getArray(e);
            filter = entity -> !CollectionUtils.contains(entities, entity);
        }

        if (pattern == 0)
            return Arrays.stream(livingEntityExpr.getArray(e))
                    .map(livingEntity -> livingEntity.rayTraceBlocks(max.doubleValue(), mode))
                    .toArray(RayTraceResult[]::new);

        if (dir == null) return null;

        return switch (pattern) {

            case 1 -> {
                if (loc == null) yield null;
                Vector vector = dir.getDirection(loc);
                yield DirectionUtils.isFinite(vector) ? CollectionUtils.array(loc.getWorld().rayTraceBlocks(loc, dir.getDirection(loc), max.doubleValue(), mode, ignore)) : null;
            }
            case 2 -> {
                if (block == null || loc == null) yield null;
                Vector vector = dir.getDirection(loc);
                yield DirectionUtils.isFinite(vector) ? CollectionUtils.array(block.rayTrace(loc, dir.getDirection(loc), max.doubleValue(), mode)) : null;
            }
            case 3 -> {
                if (box == null || position == null) yield null;
                Location location = position instanceof Location l ? l : position instanceof Vector v ? v.toLocation(Skirtness.getMainWorld()) : null;
                if (location == null) yield null;
                Vector vector = dir.getDirection(location);
                yield DirectionUtils.isFinite(vector) ? CollectionUtils.array(box.rayTrace(location.toVector(), vector, max.doubleValue())) : null;
            }
            case 4 -> {
                if (loc == null) yield null;
                Vector vector = dir.getDirection(loc);
                yield DirectionUtils.isFinite(vector) ? CollectionUtils.array(loc.getWorld().rayTraceEntities(loc, vector, max.doubleValue(), raySize.doubleValue(), filter)) : null;
            }
            case 5 -> {
                if (loc == null) yield null;
                Vector vector = dir.getDirection(loc);
                yield DirectionUtils.isFinite(vector) ? CollectionUtils.array(loc.getWorld().rayTrace(loc, vector, max.doubleValue(), mode, ignore, raySize.doubleValue(), filter)) : null;
            }
            default -> null;
        };
    }

    @Override
    public boolean isSingle() {
        return pattern != 0;
    }

    @Override
    public @NotNull Class<? extends RayTraceResult> getReturnType() {
        return RayTraceResult.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        String max = (numberExpr1 == null ? "" : ", with max " + numberExpr1.toString(e, debug)), re = (numberExpr2 == null ? "" : ", ray size " + numberExpr2.toString(e, debug)) + (entityExpr == null ? "" : ", filtering out " + entityExpr.toString(e, debug));
        return switch (pattern) {
            case 0 -> "ray trace of " + livingEntityExpr.toString(e, debug) + (collisionModeExpr == null ? "" : " using fluidcollisionmode " + collisionModeExpr.toString(e, debug)) + max;
            case 1 -> "block ray trace from " + locationExpr.toString(e, debug) + " using " + directionExpr.toString(e, debug) + (collisionModeExpr == null ? "" : ", fluidcollisionmode " + collisionModeExpr.toString(e, debug)) + (ignore ? ", to ignore passable blocks" : "") + max;
            case 2 -> "ray trace searching for " + blockExpr.toString(e, debug)  + " starting from " + locationExpr.toString(e, debug) + " using " + directionExpr.toString(e, debug) + (collisionModeExpr == null ? "" : ", fluidcollisionmode " + collisionModeExpr.toString(e, debug)) + max;
            case 3 -> "ray trace searching for " + boundingBoxExpr.toString(e, debug) + " starting from " + positionExpr.toString(e, debug) + " using " + directionExpr.toString(e, debug) + max;
            case 4 -> "entity ray trace from " + locationExpr.toString(e, debug) + " using " + directionExpr.toString(e, debug) + max + re;
            case 5 -> "generic ray trace from " + locationExpr.toString(e, debug) + " using " + directionExpr.toString(e, debug) + max + (collisionModeExpr == null ? "" : ", fluidcollisionmode " + collisionModeExpr.toString(e, debug)) + (ignore ? ", to ignore passable blocks" : "") + re;
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        switch (matchedPattern) {
            case 0 -> {
                livingEntityExpr = (Expression<LivingEntity>) exprs[0];
                collisionModeExpr = (Expression<FluidCollisionMode>) exprs[1];
                numberExpr1 = (Expression<Number>) exprs[2];
            }
            case 1 -> {
                locationExpr = (Expression<Location>) exprs[0];
                directionExpr = (Expression<Direction>) exprs[1];
                collisionModeExpr = (Expression<FluidCollisionMode>) exprs[2];
                numberExpr1 = (Expression<Number>) exprs[3];
                ignore = parseResult.mark == 1;
            }
            case 2 -> {
                blockExpr = (Expression<Block>) exprs[0];
                locationExpr = (Expression<Location>) exprs[1];
                directionExpr = (Expression<Direction>) exprs[2];
                collisionModeExpr = (Expression<FluidCollisionMode>) exprs[3];
                numberExpr1 = (Expression<Number>) exprs[4];
            }
            case 3 -> {
                boundingBoxExpr = (Expression<BoundingBox>) exprs[0];
                positionExpr = exprs[1];
                directionExpr = (Expression<Direction>) exprs[2];
                numberExpr1 = (Expression<Number>) exprs[3];
            }
            case 4 -> {
                locationExpr = (Expression<Location>) exprs[0];
                directionExpr = (Expression<Direction>) exprs[1];
                numberExpr1 = (Expression<Number>) exprs[2];
                numberExpr2 = (Expression<Number>) exprs[3];
                entityExpr = (Expression<Entity>) exprs[4];
            }
            case 5 -> {
                locationExpr = (Expression<Location>) exprs[0];
                directionExpr = (Expression<Direction>) exprs[1];
                numberExpr1 = (Expression<Number>) exprs[2];
                collisionModeExpr = (Expression<FluidCollisionMode>) exprs[3];
                numberExpr2 = (Expression<Number>) exprs[4];
                entityExpr = (Expression<Entity>) exprs[5];
                ignore = parseResult.mark == 1;
            }
        }
        pattern = matchedPattern;
        return true;
    }
}
