package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.util.VectorMath;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Getter;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Entity Direction")
@Description("Returns the direction of an entity as a vector, similar to vector from entity's yaw and pitch. Can be set")
@Examples({"set vector direction of all entities to vector(0,1,0)",
        "makes all entities look up"})
@Since("1.0.0")

public class ExprEntityDirection extends PropertyExpression<Entity, Vector> {

    static {
        Skript.registerExpression(ExprEntityDirection.class, Vector.class, ExpressionType.PROPERTY,
                "[the] vec[tor] direction[ of %entities%]",
        "%entities%'[s] vec[tor] direction");
    }

    @SuppressWarnings({"unchcked", "null"})
    @Override
    public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
        setExpr((Expression<Entity>) exprs[0]);
        return true;
    }

    @Override
    protected Vector[] get(final Event event, final Entity[] entities) {
        return get(entities, new Getter<Vector, Entity>() {
            @Override
            public Vector get(final Entity e) {
                return e.getLocation().getDirection();
            }
        });
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "entity vector direction";
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        switch (mode) {
            case ADD:
            case REMOVE:
            case SET:
                return CollectionUtils.array(Vector.class);
            default:
                return null;
        }
    }

    @Override
    public void change(final Event event, final @Nullable Object[] delta, final ChangeMode mode) {
        Entity[] entities = getExpr().getArray(event);
        if (delta != null) {
            Vector v = (Vector) delta[0];
            if (mode == ChangeMode.SET) {
                for (Entity e: entities) {
                    makeDirection(e, v);
                }
            } else {
                Vector vf;
                v = v.multiply((mode == ChangeMode.ADD) ? 1 : -1);
                for (Entity e: entities) {
                    vf = e.getLocation().getDirection().add(v);
                    makeDirection(e,vf);
                }
            }
        }
    }

    private void makeDirection(Entity e, Vector v) {
        if (e instanceof Player) {
            e.teleport(e.getLocation().setDirection(v));
        } else {
            e.setRotation((VectorMath.getYaw(v)+270),(VectorMath.getPitch(v)*-1));
        }
    }



}
