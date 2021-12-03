package io.github.mrsdarth.skirt.elements.boundingbox.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Name("Bounding Box Entities")
@Description("gets entities within a bounding box")
@Examples("kill all zombies within box {_box} in \"world\"")
@Since("1.1.0")

public class ExprBoundingBoxEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprBoundingBoxEntities.class, Entity.class, ExpressionType.COMBINED,
                "[all] %entitydatas% within [bounding] box %boundingboxes%[ in %world%]");
    }

    private Expression<EntityData<?>> entityDataExpr;
    private Expression<BoundingBox> boxExpr;
    private Expression<World> worldExpr;

    @Override
    protected @Nullable
    Entity[] get(@NotNull Event e) {
        return Skirtness.getSingle(worldExpr, e).map(world -> {
            List<EntityData<?>> entityData = Arrays.asList(entityDataExpr.getArray(e));
            return Arrays.stream(boxExpr.getArray(e))
                    .map(box -> world.getNearbyEntities(box, entity -> entityData.stream().anyMatch(type -> type.isInstance(entity))))
                    .flatMap(Collection::stream)
                    .toArray(Entity[]::new);
        }).orElse(null);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "all " + entityDataExpr.toString(e, debug) + " within bounding box " + boxExpr.toString(e, debug) + " in world " + worldExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        entityDataExpr = (Expression<EntityData<?>>) exprs[0];
        boxExpr = (Expression<BoundingBox>) exprs[1];
        worldExpr = (Expression<World>) exprs[2];
        return true;
    }
}
