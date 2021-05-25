package io.github.mrsdarth.skirt.elements.BoundingBox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Name("Random Bounding Box Point")
@Description("returns a random point in a bounding box")
@Examples({"command rtp:",
        "\ttrigger:",
        "\t\tset {_b} to bounding box between (vector 1000, 0, 1000) and (vector -1000, 0, -1000)   # the area to teleport within",
        "\t\tset {_l} to random point within {_b} as location",
        "\t\tteleport player 0.5 above highest block at {_l}"})
@Since("1.1.0")

public class ExprRandomBoundingBoxPoint extends PropertyExpression<BoundingBox, Vector> {

    static {
        Skript.registerExpression(ExprRandomBoundingBoxPoint.class, Vector.class, ExpressionType.COMBINED,
                "[a] random (point|vector) (of|within|from|in) %boundingbox%");
    }

    @Override
    public Class getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "random point in bounding box";
    }

    private Expression<BoundingBox> box;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        box = (Expression<BoundingBox>) exprs[0];
        return true;
    }

    @Override
    protected Vector[] get(Event event, BoundingBox[] boundingBoxes) {
        return get(boundingBoxes, new Converter<BoundingBox, Vector>() {
            @Nullable
            @Override
            public Vector convert(BoundingBox b) {
                b = b.clone(); Vector min = b.getMin();
                return min.add(b.getMax().subtract(min).multiply(Vector.getRandom()));
            }
        });
    }
}