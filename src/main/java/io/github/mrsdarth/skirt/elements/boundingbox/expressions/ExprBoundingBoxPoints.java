package io.github.mrsdarth.skirt.elements.boundingbox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



@Name("Bounding Box Points")
@Description("Get the upper, lower or centre point of a bounding box")
@Examples("teleport player to centre of {_box} as location")
@Since("1.0.0")

public class ExprBoundingBoxPoints extends SimplePropertyExpression<BoundingBox, Vector> {

    static {
        Skript.registerExpression(ExprBoundingBoxPoints.class, Vector.class, ExpressionType.PROPERTY,
                "cent(er|re) [vector] of %boundingboxes%",
                "(1¦upper|lower) (corner|vector) of %boundingboxes%");
    }

    private int pattern;

    @Override
    protected @NotNull String getPropertyName() {
        return switch (pattern) {
            case 1 -> "center";
            case 2 -> "lower corner";
            case 3 -> "upper corner";
            default -> throw new IllegalStateException();
        };
    }

    @Nullable
    @Override
    public Vector convert(BoundingBox boundingBox) {
        return switch (pattern) {
            case 1 -> boundingBox.getCenter();
            case 2 -> boundingBox.getMin();
            case 3 -> boundingBox.getMax();
            default -> null;
        };
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern + parseResult.mark;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }
}

