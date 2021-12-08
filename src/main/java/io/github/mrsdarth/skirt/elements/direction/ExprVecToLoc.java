package io.github.mrsdarth.skirt.elements.direction;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Vector to location")
@Description("returns a location from a vector")
@Examples("set {_locations::*} to {_vectors::*} as locations")
@Since("1.0.0")

public class ExprVecToLoc extends PropertyExpression<Vector, Location> {

    static {
        Skript.registerExpression(ExprVecToLoc.class, Location.class, ExpressionType.PROPERTY,
                "%vectors% (as|to) location[s][ in %world%]");
    }

    private Expression<World> worldExpr;

    @Override
    public @NotNull Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parser) {
        setExpr((Expression<Vector>) exprs[0]);
        worldExpr = (Expression<World>) exprs[1];
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getExpr().toString(e, debug) + " as location in " + worldExpr.toString(e, debug);
    }


    @Override
    protected Location @Nullable [] get(@NotNull Event event, Vector @NotNull [] vectors) {
        return Skirtness.getSingle(worldExpr, event).map(world -> get(vectors, vector -> vector.toLocation(world))).orElse(null);
    }
}