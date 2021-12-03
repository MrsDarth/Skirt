package io.github.mrsdarth.skirt.elements.direction;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.VectorMath;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.reflect.effects.EffMoveEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


@Name("Entity Direction")
@Description("Returns the direction of an entity as a vector, similar to vector from entity's yaw and pitch. Can be set")
@Examples({"add 10 to vector yaw of player's direction",
        "# rotates the player by 10"})
@Since("1.0.0")

public class ExprDirectionOf extends SimplePropertyExpression<Object, Vector> {

    static {
        register(ExprDirectionOf.class, Vector.class, "[vector] direction", "entities/locations");
    }



    @Override
    protected @NotNull String getPropertyName() {
        return "direction";
    }

    @Nullable
    @Override
    public Vector convert(Object o) {
        return o instanceof Location location ? location.getDirection() : o instanceof Entity entity ? entity.getLocation().getDirection() : null;
    }

    @Override
    public @NotNull Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(final @NotNull ChangeMode mode) {
        return mode == ChangeMode.SET ? CollectionUtils.array(Vector.class, Direction.class) : null;
    }

    @Override
    public void change(final @NotNull Event e, final Object @Nullable [] delta, final @NotNull ChangeMode mode) {
        if (delta == null || delta.length == 0) return;
        Object dir = delta[0];

        for (Object o: getExpr().getArray(e)) {

            if (o instanceof Entity entity) {

                Optional.ofNullable(dir instanceof Vector v ? v : dir instanceof Direction direction ? direction.getDirection(entity) : null)
                        .ifPresent(vector -> {
                            if (entity instanceof Player player) {
                                if (Skirtness.hasProtocolLib()) EffMoveEntity.move(player, player.getLocation().setDirection(vector));
                            } else
                                entity.setRotation(VectorMath.skriptYaw(VectorMath.getYaw(vector)), VectorMath.skriptPitch(VectorMath.getPitch(vector)));
                        });

            } else if (o instanceof Location location) {

                Optional.ofNullable(dir instanceof Vector v ? v : dir instanceof Direction direction ? direction.getDirection(location) : null)
                        .ifPresent(location::setDirection);

            }

        }

    }

}
