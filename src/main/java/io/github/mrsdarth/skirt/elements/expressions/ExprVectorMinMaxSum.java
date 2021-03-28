package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;

import org.bukkit.event.Event;
import ch.njol.util.Kleenean;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;


public class ExprVectorMinMaxSum extends SimpleExpression<Vector> {

    static {
        Skript.registerExpression(ExprVectorMinMaxSum.class, Vector.class, ExpressionType.COMBINED,
                "(1¦max|2¦min)[imum] of vec[tor][s] %vectors%",
                "sum of [vector[s]] %vectors%");
    }

    private Expression<Vector> vecs;
    private boolean max;
    private boolean mm;

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        mm = (matchedPattern == 0);
        vecs = (Expression<Vector>) exprs[0];
        if (mm) max = (parser.mark == 1);
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "vector min/max/sum";
    }

    @Override
    @Nullable
    protected Vector[] get(Event event) {
        Vector[] vectors = vecs.getArray(event);
        Vector result;
        if (vectors[0] != null) {
            result = vectors[0].clone();
            if (mm) {
                for (Vector v: vectors) {
                    result = result.add(v);
                }
            } else if (max) {
                for (Vector v : vectors) {
                    result = Vector.getMaximum(result, v);
                }
            } else {
                for (Vector v : vectors) {
                    result = Vector.getMinimum(result, v);
                }
            }
            return new Vector[] {result};
        }
        return null;
    }
}