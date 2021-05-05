package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class ExprRandomBoundingBoxPoint extends SimpleExpression {

    static {
        Skript.registerExpression(ExprRandomBoundingBoxPoint.class, Vector.class, ExpressionType.COMBINED,
                "[a] random (point|vector) (of|within|from|in) %boundingbox%");
    }

    @Nullable
    @Override
    protected Vector[] get(Event event) {
        BoundingBox b = box.getSingle(event);
        if (b == null) return null; else b = b.clone();
        Vector min = b.getMin();
        return new Vector[]{min.add(b.getMax().subtract(min).multiply(Vector.getRandom()))};
    }

    @Override
    public boolean isSingle() {
        return true;
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
}
