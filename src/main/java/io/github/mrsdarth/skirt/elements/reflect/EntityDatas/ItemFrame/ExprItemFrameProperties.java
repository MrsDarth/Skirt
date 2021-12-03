package io.github.mrsdarth.skirt.elements.reflect.EntityDatas.ItemFrame;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Skirtness;
import io.github.mrsdarth.skirt.elements.direction.DirectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Item Frame Properties")
@Description({
        "Get or set an item frame's:",
        "rotation - the rotation of the item in the frame",
        "facing - the direction of the item frame itself",
        "fixed state - whether the item frame can be destroyed by regular means",
        "visibility - whether players can see the item frame"
})
@Examples("set item frame rotation of ")
@Since("1.2.2")

public class ExprItemFrameProperties extends SimplePropertyExpression<Entity, Object> {

    static {
        register(ExprItemFrameProperties.class, Object.class, "item[ ]frame (rotation|1¦facing|2¦fixed state|3¦visibility)", "entities");
    }

    private int pattern;

    @Override
    protected @NotNull String getPropertyName() {
        return switch (pattern) {
            case 0 -> "rotation";
            case 1 -> "facing";
            case 2 -> "fixed state";
            case 3 -> "visibility";
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public @Nullable Object convert(Entity entity) {
        return entity instanceof ItemFrame itemFrame ? switch (pattern) {
            case 0 -> itemFrame.getRotation().ordinal();
            case 1 -> new Direction(itemFrame.getFacing(), 1);
            case 2 -> itemFrame.isFixed();
            case 3 -> itemFrame.isVisible();
            default -> throw new IllegalStateException();
        } : null;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return switch (pattern) {
            case 0 -> Long.class;
            case 1 -> Direction.class;
            case 2, 3 -> Boolean.class;
            default -> throw new IllegalStateException();
        };
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return pattern != 0 ? CollectionUtils.array(getReturnType()) : null;
        else if (mode != Changer.ChangeMode.REMOVE_ALL)
            return pattern == 0 ? CollectionUtils.array(Number.class) : null;
        return null;
    }

    @Override
    public void change(@NotNull Event event, Object @Nullable [] delta, Changer.@NotNull ChangeMode mode) {

        for (Entity entity : getExpr().getArray(event))
            if (entity instanceof ItemFrame itemFrame) {
                if (pattern == 0) {
                    if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET)
                        itemFrame.setRotation(Rotation.NONE);
                    else if (!ArrayUtils.isEmpty(delta) && delta[0] instanceof Number number) {
                        int change = number.intValue();
                        itemFrame.setRotation(Skirtness.enumAt(Rotation.class, switch (mode) {
                            case SET -> change;
                            case ADD -> itemFrame.getRotation().ordinal() + change;
                            case REMOVE -> itemFrame.getRotation().ordinal() - change;
                            default -> throw new IllegalStateException();
                        }));
                    }
                } else if (!ArrayUtils.isEmpty(delta)) {
                    if (pattern == 1) {
                        if (delta[0] instanceof Direction direction)
                            itemFrame.setFacingDirection(DirectionUtils.nearestBlockFace(direction.getDirection(itemFrame)));
                    } else if (delta[0] instanceof Boolean state) {
                        if (pattern == 2) itemFrame.setFixed(state);
                        else itemFrame.setVisible(state);
                    }
                }
            }

    }


}
