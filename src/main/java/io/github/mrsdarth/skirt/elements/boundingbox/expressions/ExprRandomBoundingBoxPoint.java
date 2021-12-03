package io.github.mrsdarth.skirt.elements.boundingbox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Random Bounding Box Point")
@Description("returns a random point in a bounding box")
@Examples({"command rtp:",
        "\ttrigger:",
        "\t\tset {_b} to bounding box between (vector 1000, 0, 1000) and (vector -1000, 0, -1000)   # the area to teleport within",
        "\t\tset {_l} to random point within {_b} as location",
        "\t\tteleport player 0.5 above highest block at {_l}"})
@Since("1.1.0")

public class ExprRandomBoundingBoxPoint extends SimplePropertyExpression<BoundingBox, Vector> {

    static {
        Skript.registerExpression(ExprRandomBoundingBoxPoint.class, Vector.class, ExpressionType.PROPERTY,
                "[a] random (point|vector) (of|within|from|in) %boundingbox%");
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "random point";
    }

    @Nullable
    @Override
    public Vector convert(BoundingBox boundingBox) {
        Vector min = boundingBox.getMin(), max = boundingBox.getMax();
        return min.add(max.subtract(min).multiply(Vector.getRandom()));
    }

}