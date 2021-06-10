package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;

import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import ch.njol.util.Kleenean;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Vector Min/Max/Sum")
@Description("Returns the maximum, minimum or sum of a list of vectors")
@Examples({"set {_max} to max of vectors (vector(1,2,3) and vector(2,0,1)) #returns vector(2,2,3)",
        "set {_sum} to sum of vectors (vector(1,2,3) and vector(2,0,1)) #returns vector(3,2,4)"})
@Since("1.0.0")

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
            return CollectionUtils.array(result);
        }
        return null;
    }
}