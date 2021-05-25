package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.VectorMath;
import io.github.mrsdarth.skirt.Main;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.event.Event;


import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.util.coll.CollectionUtils;

import org.jetbrains.annotations.Nullable;

import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;


@Name("Entity Direction")
@Description("Returns the direction of an entity as a vector, similar to vector from entity's yaw and pitch. Can be set")
@Examples({"add 10 to vector yaw of player's direction",
        "# rotates the player by 10"})
@Since("1.0.0")

public class ExprEntityDirection extends SimplePropertyExpression<Entity, Vector> {

    static {
        register(ExprEntityDirection.class, Vector.class, "[vector] direction", "entities");
    }

    @Override
    protected String getPropertyName() {
        return "direction";
    }

    @Nullable
    @Override
    public Vector convert(Entity entity) {
        return entity.getLocation().getDirection();
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(final ChangeMode mode) {
        return mode == ChangeMode.SET ? CollectionUtils.array(Vector.class, Direction.class) : null;
    }

    @Override
    public void change(final Event event, final @Nullable Object[] delta, final ChangeMode mode) {
        Entity[] entities = getExpr().getArray(event);
            Vector v = (delta[0] instanceof Vector) ? (Vector) delta[0] : ((Direction) delta[0]).getDirection(Reflectness.zeroloc);
            float
                    yaw = VectorMath.skriptYaw(VectorMath.getYaw(v)),
                    pitch = VectorMath.skriptPitch(VectorMath.getPitch(v));
            try {
                if (mode == ChangeMode.SET) {
                    for (Entity e : entities) {
                        if (e instanceof Player) {
                            Reflectness.move(e, e.getLocation().setDirection(v));
                        } else {
                            e.setRotation(yaw, pitch);
                        }
                    }
                }
            } catch (Exception ex) {ex.printStackTrace();}
    }



}
