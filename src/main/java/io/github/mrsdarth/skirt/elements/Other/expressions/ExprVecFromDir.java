package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import io.github.mrsdarth.skirt.Main;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Vector from direction")
@Description("Converts a skript direction to a vector, useful if you want to manipulate a direction")
@Examples({"set {_v} to vector from direction (facing of player)",
        "loop 360 times:",
        "\tadd 1 to vector yaw of {_v}"})
@Since("1.0.0")

public class ExprVecFromDir extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprVecFromDir.class, Object.class, ExpressionType.SIMPLE,
                "vector[s] (of|from) dir[ection][s] %directions%"
        );
    }


    Expression<Direction> dir;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        dir = (Expression<Direction>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected Vector[] get(Event e) {
        int size;
        ArrayList<Vector> vecs = new ArrayList<Vector>();
        for (Direction d: dir.getArray(e)) {
            vecs.add(d.getDirection(Reflectness.zeroloc));
        }
        return vecs.toArray(new Vector[vecs.size()]);

    }


    @Override
    public boolean isSingle() {
        return dir.isSingle();
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "vector direction";
    }

}
