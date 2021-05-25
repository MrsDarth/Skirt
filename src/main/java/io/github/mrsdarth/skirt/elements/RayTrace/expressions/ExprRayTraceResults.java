package io.github.mrsdarth.skirt.elements.RayTrace.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Ray Trace Results")
@Description("The results of a ray trace")
@Examples("if hit face of {_ray} = north:")
@Since("1.0.0")


public class ExprRayTraceResults extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprRayTraceResults.class, Object.class, ExpressionType.SIMPLE,
                "[the] hit block[s] of %raytraceresults%",
                "[the] hit (vector|position) of %raytraceresults%",
                "[the] hit [block[ ]]fac(e|ing)[s] of %raytraceresults%",
                "[the] hit entit(y|ies) of %raytraceresults%"
        );
    }


    private int pattern;
    private Expression<RayTraceResult> ray;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        pattern = matchedPattern;
        ray = (Expression<RayTraceResult>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Object[] get(Event e) {
        RayTraceResult[] rays = ray.getArray(e);
        Block block;
        switch (pattern) {
            case 0:
                ArrayList<Block> blocks = new ArrayList<Block>();
                for (RayTraceResult r: rays) {
                    blocks.add(r.getHitBlock());
                }
                return blocks.toArray(new Block[blocks.size()]);


            case 1:
                ArrayList<Vector> vec = new ArrayList<Vector>();
                for (RayTraceResult r: rays) {
                    vec.add(r.getHitPosition());
                }
                return vec.toArray(new Vector[vec.size()]);

            case 2:
                ArrayList<Direction> dirs = new ArrayList<Direction>();
                Direction d;
                BlockFace f;
                for (RayTraceResult r: rays) {
                    f = r.getHitBlockFace();
                    if (f != null) {
                        dirs.add(new Direction(f, 1));
                    }
                }
                return dirs.toArray(new Direction[dirs.size()]);

            case 3:
                ArrayList<Entity> es = new ArrayList<Entity>();
                Entity entity;
                for (RayTraceResult r: rays) {
                    entity = r.getHitEntity();
                    if (entity != null) {
                        es.add(entity);
                    }
                }
                return es.toArray(new Entity[es.size()]);
        }
        return null;

    }


    @Override
    public boolean isSingle() {
        return ray.isSingle();
    }

    @Override
    public Class<? extends Object> getReturnType() {
        switch (pattern) {
            case 0: return Block.class;
            case 1: return Vector.class;
            case 2: return Direction.class;
            case 3: return Entity.class;
        }
        return null;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Ray Trace Properties";
    }

}
