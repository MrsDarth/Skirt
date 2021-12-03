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
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


@Name("Vector from direction")
@Description("Converts a skript direction to a vector, useful if you want to manipulate a direction")
@Examples({"set {_v} to vector from direction (facing of player)",
        "loop 360 times:",
        "\tadd 1 to vector yaw of {_v}"})
@Since("1.0.0")

public class ExprVecFromDir extends PropertyExpression<Direction, Vector> {

    static {
        Converters.registerConverter(Direction.class, Vector.class, DirectionUtils::vectorFromDirectionStrict);
        Skript.registerExpression(ExprVecFromDir.class, Vector.class, ExpressionType.PROPERTY,
                "vector[s] (of|from) dir[ection][s] %directions%");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends Direction>) exprs[0]);
        return true;
    }

    @Override
    protected Vector @NotNull [] get(@NotNull Event event, Direction @NotNull [] directions) {
        return get(directions, DirectionUtils::vectorFromDirectionStrict);
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }


    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "vector from direction " + getExpr().toString(e, debug);
    }

}
