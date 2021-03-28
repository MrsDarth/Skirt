package io.github.mrsdarth.sk69.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ExprBoundingBoxPoints extends SimpleExpression<Vector> {

    static {
        Skript.registerExpression(ExprBoundingBoxPoints.class, Vector.class, ExpressionType.SIMPLE,
                "cent(er|re) [vector] of %boundingboxes%",
                "(1¦upper|2¦lower) corner of %boundingboxes%");
    }


    private int pattern;
    private int mark;
    private Expression<BoundingBox> box;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        pattern = matchedPattern;
        mark = parseResult.mark;
        box = (Expression<BoundingBox>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Vector[] get(Event e) {
        ArrayList<Vector> vecs = new ArrayList<Vector>();
        for (BoundingBox b: box.getArray(e)) {
            vecs.add((pattern == 0) ? b.getCenter() : (mark == 1) ? b.getMax() : b.getMin());
        }
        return vecs.toArray(new Vector[vecs.size()]);

    }


    @Override
    public boolean isSingle() {
        return box.isSingle();
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "BoundingBox Points";
    }

}

