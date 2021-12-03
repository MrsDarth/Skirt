package io.github.mrsdarth.skirt.elements.boundingbox.expressions;


import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;


@Name("Create Bounding Box")
@Description("Creates a bounding box, bounding box of block may not be accurate for blocks like stairs, however collision shape returns a list of bounding boxes representing the exact shape, read [this](https://github.com/MrsDarth/skirt/wiki/BoundingBox) for more info.")
@Examples({"set {_box} to new bounding box with centre vector(0,0,0) with dimensions 1, 2, 3",
        "# creates a bounding box between vector 1, 2, 3 and vector -1, -2, -3"})
@Since("1.0.0")

public class ExprBoundingBox extends SimpleExpression<BoundingBox> {

    static {
        Skript.registerExpression(ExprBoundingBox.class, BoundingBox.class, ExpressionType.COMBINED,
                "(bounding|hit)[ ]box of %entities%",
                "(bounding|hit)[ ]box of %blocks%",
                "collision shape of %blocks%",
                "[new ]bounding box (from|between|within) %vector/location% (and|to) %vector/location%",
                "[new ]bounding box (with centre|at) %vector/location% [with dimensions] [x] %number%, [y] %number%, [z] %number%",
                "%boundingboxes% [with all directions] expanded by %number%",
                "intersection [box] (between|of) %boundingbox% and %boundingbox%");
    }


    @Override
    public @NotNull Class<? extends BoundingBox> getReturnType() {
        return BoundingBox.class;
    }

    private Expression<Entity> entityExpr;
    private Expression<Block> blockExpr;
    private Expression<?> positionExpr1, positionExpr2;
    private Expression<Number> numExpr1, numExpr2, numExpr3;
    private Expression<BoundingBox> boxExpr1, boxExpr2;

    private int pattern;

    @Override
    public boolean isSingle() {
        return switch (pattern) {
            case 0 -> entityExpr.isSingle();
            case 1 -> blockExpr.isSingle();
            case 2 -> false;
            case 3, 4, 6 -> true;
            case 5 -> boxExpr1.isSingle();
            default -> throw new IllegalStateException();
        };
    }


    @Override
    protected @Nullable
    BoundingBox[] get(@NotNull Event e) {

        return switch (pattern) {

            case 0 -> Arrays.stream(entityExpr.getArray(e))
                    .map(Entity::getBoundingBox)
                    .toArray(BoundingBox[]::new);

            case 1 -> Arrays.stream(blockExpr.getArray(e))
                    .map(Block::getBoundingBox)
                    .toArray(BoundingBox[]::new);

            case 2 -> Arrays.stream(blockExpr.getArray(e))
                    .map(Block::getCollisionShape)
                    .map(VoxelShape::getBoundingBoxes)
                    .flatMap(Collection::stream)
                    .toArray(BoundingBox[]::new);

            case 3 -> {
                Vector pos1 = getAsVector(positionExpr1, e), pos2 = getAsVector(positionExpr2, e);
                yield (pos1 == null || pos2 == null) ? null : CollectionUtils.array(BoundingBox.of(pos1, pos2));
            }

            case 4 -> {
                Vector centre = getAsVector(positionExpr1, e);
                Number x = numExpr1.getSingle(e), y = numExpr2.getSingle(e), z = numExpr3.getSingle(e);
                yield (centre == null || x == null || y == null || z == null) ? null : CollectionUtils.array(BoundingBox.of(centre, x.doubleValue(), y.doubleValue(), z.doubleValue()));
            }

            case 5 -> {
                Number number = numExpr1.getSingle(e);
                if (number == null) yield null;
                double expansion = number.doubleValue();
                yield Arrays.stream(boxExpr1.getArray(e))
                        .map(box -> box.expand(expansion))
                        .toArray(BoundingBox[]::new);
            }

            case 6 -> {
                BoundingBox box1 = boxExpr1.getSingle(e), box2 = boxExpr2.getSingle(e);
                yield (box1 == null || box2 == null) ? null : CollectionUtils.array(box1.clone().intersection(box2));
            }

            default -> null;
        };
    }

    private static Vector getAsVector(Expression<?> vecOrLocExpr, Event e) {
        Object object = vecOrLocExpr.getSingle(e);
        return object instanceof Vector vector ? vector : object instanceof Location location ? location.toVector() : null;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return switch (pattern) {
            case 0 -> "bounding box of " + entityExpr.toString(e, debug);
            case 1 -> "bounding box of " + blockExpr.toString(e, debug);
            case 2 -> "collision shape of " + blockExpr.toString(e, debug);
            case 3 -> "new bounding box from " + positionExpr1.toString(e, debug) + " to " + positionExpr2.toString(e, debug);
            case 4 -> "bounding box at " + positionExpr1.toString(e, debug) + " with dimensions " + numExpr1.toString(e, debug) + ", " + numExpr2.toString(e, debug) + ", " + numExpr3.toString(e, debug);
            case 5 -> boxExpr1.toString(e, debug) + " with all directions expanded by " + numExpr1.toString(e, debug);
            case 6 -> "intersection between " + boxExpr1.toString(e, debug) + " and " + boxExpr2.toString(e, debug);
            default -> throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        pattern = matchedPattern;
        switch (matchedPattern) {
            case 0 -> entityExpr = (Expression<Entity>) exprs[0];
            case 1, 2 -> blockExpr = (Expression<Block>) exprs[0];
            case 3 -> {
                positionExpr1 = exprs[0];
                positionExpr2 = exprs[1];
            }
            case 4 -> {
                positionExpr1 = exprs[0];
                numExpr1 = (Expression<Number>) exprs[1];
                numExpr2 = (Expression<Number>) exprs[2];
                numExpr3 = (Expression<Number>) exprs[3];
            }
            case 5 -> {
                boxExpr1 = (Expression<BoundingBox>) exprs[0];
                numExpr1 = (Expression<Number>) exprs[1];
            }
            case 6 -> {
                boxExpr1 = (Expression<BoundingBox>) exprs[0];
                boxExpr2 = (Expression<BoundingBox>) exprs[1];
            }
        }
        return true;
    }

}

