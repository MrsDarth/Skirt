package io.github.mrsdarth.sk69.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ExprVecToDir extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprVecToDir.class, Object.class, ExpressionType.SIMPLE,
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
        Location l = new Location(Bukkit.getServer().getWorlds().get(0),0,0,0);
        for (Direction d: dir.getArray(e)) {
            vecs.add(d.getDirection(l));
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
