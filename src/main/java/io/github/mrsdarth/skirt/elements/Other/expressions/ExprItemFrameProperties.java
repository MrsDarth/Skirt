package io.github.mrsdarth.skirt.elements.Other.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.ExprFacing;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

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
        register(ExprItemFrameProperties.class, Object.class,
                "item[ ]frame (1¦rotation|2¦facing|3¦fixed state|4¦visibility)",
                "entities");
    }

    private int pattern;

    private Object getproperty(ItemFrame e, int mark) {
        switch (mark) {
            case 1: return CollectionUtils.indexOf(Rotation.values(), e.getRotation());
            case 2: return new Direction(e.getFacing(), 1);
            case 3: return e.isFixed();
            case 4: return e.isVisible();
        } return null;
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        pattern = parseResult.mark;
        return true;
    }

    @Override
    protected String getPropertyName() {
        return "item frame rotation";
    }

    @Nullable
    @Override
    public Object convert(Entity e) {
        return e instanceof ItemFrame ? getproperty((ItemFrame) e, pattern) : null;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        switch (pattern) {
            case 1: return Number.class;
            case 2: return Direction.class;
            case 3:
            case 4: return Boolean.class;
        }
        return Object.class;
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (pattern) {
            case 1:
                return (mode != Changer.ChangeMode.REMOVE_ALL) ? CollectionUtils.array(Number.class) : null;
            case 2:
                return (mode == Changer.ChangeMode.SET) ? CollectionUtils.array(Direction.class) : null;
            default:
                return (mode == Changer.ChangeMode.SET) ? CollectionUtils.array(Boolean.class) : null;
        }
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, Changer.ChangeMode mode) {
        Rotation[] rotations = Rotation.values();
        int size = rotations.length;
        BlockFace blockFace = null;
        if (pattern == 2 && delta[0] instanceof Direction) {
            try {
                Method toblockface = ExprFacing.class.getDeclaredMethod("toBlockFace", Vector.class);
                toblockface.setAccessible(true);
                blockFace = (BlockFace) toblockface.invoke(null, ((Direction) delta[0]).getDirection(ExprVecFromDir.zero()));
            } catch (Exception ex) {}
        }
        for (Entity e: getExpr().getArray(event)) {
            if (!(e instanceof ItemFrame)) continue;
            ItemFrame frame = (ItemFrame) e;
            switch (pattern) {
                case 1:
                    int i = delta[0] instanceof Number ? ((Number) delta[0]).intValue() : 0;
                    if (mode == Changer.ChangeMode.ADD) i += ((Number) getproperty(frame,1)).intValue();
                    else if (mode == Changer.ChangeMode.REMOVE) i -= ((Number) getproperty(frame,1)).intValue();
                    frame.setRotation(rotations[i % size]);
                    break;
                case 2:
                    if (delta[0] instanceof Direction) frame.setFacingDirection(blockFace, true);
                    break;
                case 3:
                    if (delta[0] instanceof Boolean) frame.setFixed((Boolean) delta[0]);
                    break;
                case 4:
                    if (delta[0] instanceof Boolean) frame.setVisible((Boolean) delta[0]);
            }
        }
    }
}
