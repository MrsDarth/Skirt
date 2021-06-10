package io.github.mrsdarth.skirt.elements.RayTrace.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import org.bukkit.Location;
import org.bukkit.World;
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


public class ExprRayTraceResults extends SimplePropertyExpression<RayTraceResult,Object> {

    static {
        register(ExprRayTraceResults.class,Object.class,
                "hit (1¦block[s]|2¦(vector|position)[s]|3¦location|4¦[block[ ]]fac(e|ing)[s]|5¦entit(y|ies))", "raytraceresults"
        );
    }

    private int type;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        type = parseResult.mark;
        return true;
    }

    @Override
    protected String getPropertyName() {
        switch (type) {
            case 1: return "hit block";
            case 2: return "hit position";
            case 3: return "hit location";
            case 4: return "hit blockface";
            case 5: return "hit entity";
        }
        return null;
    }

    @Nullable
    @Override
    public Object convert(RayTraceResult ray) {
        switch (type) {
            case 1: return ray.getHitBlock();
            case 2: return ray.getHitPosition();
            case 3:
                Block b = ray.getHitBlock();
                Entity e = ray.getHitEntity();
                World world = (b != null) ? b.getWorld() :
                        (e != null) ? e.getWorld() : null;
                return (world != null) ? ray.getHitPosition().toLocation(world) : null;
            case 4:
                BlockFace face = ray.getHitBlockFace();
                return face != null ? new Direction(face,1) : null;
            case 5:
                return ray.getHitEntity();
        }
        return null;
    }


    @Override
    public Class<? extends Object> getReturnType() {
        switch (type) {
            case 1: return Block.class;
            case 2: return Vector.class;
            case 3: return Location.class;
            case 4: return Direction.class;
            case 5: return Entity.class;
        }
        return null;
    }

}
