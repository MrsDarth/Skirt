package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import ch.njol.util.Kleenean;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Vector to location")
@Description("returns a location from a vector")
@Examples("set {_locations::*} to {_vectors::*} as locations")
@Since("1.0.0")

public class ExprVecToLoc extends SimpleExpression<Location> {

    static {
        Skript.registerExpression(ExprVecToLoc.class, Location.class, ExpressionType.COMBINED,
                "%vectors% as location[s][ in %world%]"/*,
                "[new] (zero|0|empty) location[ in %worlds%]"*/);
    }

    private Expression<Vector> vecs;
    private Expression<World> world;
    private boolean withv;

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
        withv = (matchedPattern == 0);
        if (withv) {
            vecs = (Expression<Vector>) exprs[0];
            world = (Expression<World>) exprs[1];
        } else {
            world = (Expression<World>) exprs[0];
        }
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vector to location";
    }

    @Override
    @Nullable
    protected Location[] get(Event event) {
        ArrayList<Location> locations = new ArrayList<Location>();
        if (withv) {
            World w = world.getSingle(event);
            if (w == null) {
                return null;
            }
            for (Vector v: vecs.getArray(event)) {
                locations.add(v.toLocation(w));
            }
        } else {
            for (World w: world.getArray(event)) {
                locations.add(new Location(w, 0, 0, 0));
            }
        }
        return locations.toArray(new Location[locations.size()]);
    }
}