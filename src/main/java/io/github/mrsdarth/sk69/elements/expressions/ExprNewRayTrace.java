package io.github.mrsdarth.sk69.elements.expressions;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;


public class ExprNewRayTrace extends SimpleExpression<RayTraceResult> {

    static {
        Skript.registerExpression(ExprNewRayTrace.class, RayTraceResult.class, ExpressionType.COMBINED,
                "ray[ ]trace[s] (of|from) %livingentities% using [fluid[collision][mode]] %fluidcollisionmode%[, with max %-number%]",
                "block ray[ ]trace[ starting] from %location% using %vector%, [fluid[collision][mode]] %fluidcollisionmode%[, (1¦(to ignore|ignoring) passable[ blocks])][, with max %-number%]",
                "ray[ ]trace for[ this specific] %block% [starting] from %location% using %vector%, [fluid[collision][mode]] %fluidcollisionmode%[, with max %-number%]",
                "ray[ ]trace for[ this specific] %boundingbox% [starting] from %vector% using %vector%[, with max %-number%]",
                "entity ray[ ]trace [starting] from %location% using %vector%[, with max %-number%][, ray[ ]size %-number%][, filter[ing][ out]  %-entities%]",
                "[block and entity ]ray[ ]trace [starting] from %location% using %vector%, max %number%, [fluid[collision][mode]] %fluidcollisionmode%[, (1¦(to ignore|ignoring) passable[ blocks])], ray[ ]size %number%[, filter[ing][ out]  %-entities%]");
    }



    @Override
    public Class<? extends RayTraceResult> getReturnType() {
        return RayTraceResult.class;
    }

    @Override
    public boolean isSingle() {
        return (pattern != 0 || livingentities.isSingle());
    }

    private Expression<LivingEntity> livingentities;
    private Expression<Entity> entities;
    private Expression<Location> location;
    private Expression<Vector> start;
    private Expression<Vector> vector;
    private Expression<Block> block;
    private Expression<BoundingBox> boundingbox;
    private Expression<FluidCollisionMode> fmode;
    private Expression<Number> max;
    private Expression<Number> raysize;

    private boolean ignore;

    private int pattern;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        pattern = matchedPattern;
        switch (pattern) {
            case 0:
                livingentities = (Expression<LivingEntity>) exprs[0];
                fmode = (Expression<FluidCollisionMode>) exprs[1];
                max = (Expression<Number>) exprs[2];
                break;

            case 1:
                location = (Expression<Location>) exprs[0];
                vector = (Expression<Vector>) exprs[1];
                fmode = (Expression<FluidCollisionMode>) exprs[2];
                ignore = (parser.mark == 1);
                max = (Expression<Number>) exprs[3];
                break;

            case 2:
                block = (Expression<Block>) exprs[0];
                location = (Expression<Location>) exprs[1];
                vector = (Expression<Vector>) exprs[2];
                fmode = (Expression<FluidCollisionMode>) exprs[3];
                max = (Expression<Number>) exprs[4];
                break;

            case 3:
                boundingbox = (Expression<BoundingBox>) exprs[0];
                start = (Expression<Vector>) exprs[1];
                vector = (Expression<Vector>) exprs[2];
                max = (Expression<Number>) exprs[3];
                break;

            case 4:
                location = (Expression<Location>) exprs[0];
                vector = (Expression<Vector>) exprs[1];
                max = (Expression<Number>) exprs[2];
                raysize = (Expression<Number>) exprs[3];
                entities = (Expression<Entity>) exprs[4];
                break;

            case 5:
                location = (Expression<Location>) exprs[0];
                vector = (Expression<Vector>) exprs[1];
                max = (Expression<Number>) exprs[2];
                fmode = (Expression<FluidCollisionMode>) exprs[3];
                raysize = (Expression<Number>) exprs[4];
                entities = (Expression<Entity>) exprs[5];
                ignore = (parser.mark == 1);

        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return ("Ray trace");
    }

    @Override
    @Nullable
    protected RayTraceResult[] get(Event event) {
        Expression<?>[][] exprs = {{livingentities,fmode},{location,vector,fmode},{block,location,vector,fmode},{boundingbox,start,vector},{location,vector},{location,vector,max,fmode,raysize}};
        for (Expression<?> expression: exprs[pattern]) {
            if (expression.getSingle(event) == null) {
                return null;
            }
        }
        Number maxn = (max != null) ? max.getSingle(event) : 100;
        if (maxn == null) {
            return null;
        }
        double maxd = maxn.doubleValue();
        Predicate<Entity> predicate = null;
        switch (pattern) {
            case 0:
                ArrayList<RayTraceResult> rays = new ArrayList<RayTraceResult>();
                for (LivingEntity l: livingentities.getArray(event)) {
                    rays.add(l.rayTraceBlocks(maxd,fmode.getSingle(event)));
                }
                return rays.toArray(new RayTraceResult[rays.size()]);

            case 1:
                Location loc = location.getSingle(event);
                return new RayTraceResult[] {loc.getWorld().rayTraceBlocks(loc, vector.getSingle(event), maxd, fmode.getSingle(event), ignore)};

            case 2:
                return new RayTraceResult[] {block.getSingle(event).rayTrace(location.getSingle(event), vector.getSingle(event), maxd, fmode.getSingle(event))};

            case 3:
                return new RayTraceResult[] {boundingbox.getSingle(event).rayTrace(start.getSingle(event), vector.getSingle(event), maxd)};

            case 4:
                Double rs;
                if (entities != null) {
                    predicate = pred(entities.getArray(event));
                }
                if (raysize != null) {
                    Number n = raysize.getSingle(event);
                    if (n == null) {
                        return null;
                    }
                    rs = n.doubleValue();
                } else {
                    rs = null;
                }
                Location loc1 = location.getSingle(event);
                World world = loc1.getWorld();
                Vector v1 = vector.getSingle(event);
                if (rs != null) {
                    return new RayTraceResult[] {world.rayTraceEntities(loc1, v1, maxd, rs, predicate)};
                }
                return new RayTraceResult[] {world.rayTraceEntities(loc1, v1, maxd, predicate)};

            case 5:
                if (entities != null) {
                    predicate = pred(entities.getArray(event));
                }
                return new RayTraceResult[] {location.getSingle(event).getWorld().rayTrace(location.getSingle(event), vector.getSingle(event), maxd, fmode.getSingle(event), ignore, raysize.getSingle(event).doubleValue(), predicate)};
        }
        return null;

    }

    private Predicate<Entity> pred(@Nullable Entity[] es) {
        return e -> {
            String uuid = e.getUniqueId().toString();
            boolean match;
            for (Entity entity: es) {
                if (entity.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                    return false;
                }
            }
            return true;
        };
    }

}
