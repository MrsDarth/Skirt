package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;


@Name("Vector to location")
@Description("returns a location from a vector")
@Examples("set {_locations::*} to {_vectors::*} as locations")
@Since("1.0.0")

public class ExprVecToLoc extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprVecToLoc.class, Location.class, ExpressionType.COMBINED,
                "%vectors% as location[s][ in %world%]");
    }

    private Expression<Vector> vecs;
    private Expression<World> world;

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public boolean isSingle() {
        return world.isSingle();
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        vecs = (Expression<Vector>) exprs[0];
        world = (Expression<World>) exprs[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vector to location";
    }

    @Override
    @Nullable
    protected Location[] get(Event event) {
        World w = world.getSingle(event);
        if (w == null) return null;
        Vector[] vectors = vecs.getArray(event);
        Location[] locations = new Location[vectors.length];
        int i = 0;
        for (Vector v: vectors) {
            locations[i++] = v.toLocation(w);
        }
        return locations;
    }
}