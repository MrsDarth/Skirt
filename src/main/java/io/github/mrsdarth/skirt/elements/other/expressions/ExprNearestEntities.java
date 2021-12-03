package io.github.mrsdarth.skirt.elements.other.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

@Name("Nearest Entities")
@Description("get the nearest x entities to a location sorted")
@Examples("give diamond to nearest player from {prize}")
@Since("1.1.0")

public class ExprNearestEntities extends SimpleExpression<Entity> {

    static {
        Skript.registerExpression(ExprNearestEntities.class, Entity.class, ExpressionType.COMBINED,
                "[the] nearest [%-number%] %entitydatas% (from|of|to) %location%");
    }

    private Expression<Number> numberExpr;
    private Expression<EntityData<?>> entityDataExpr;
    private Expression<Location> locationExpr;

    @Override
    protected @Nullable
    Entity[] get(@NotNull Event e) {
        Number number = numberExpr == null ? 1 : numberExpr.getSingle(e);
        EntityData<?>[] types = entityDataExpr.getAll(e);
        Location location = locationExpr.getSingle(e);
        int limit;
        if (number == null || location == null || types.length == 0 || (limit = number.intValue()) < 1) return null;
        return location.getWorld().getEntities().stream()
                .filter(entity -> {
                    for (EntityData<?> type: types)
                        if (type.isInstance(entity)) return true;
                    return false;
                })
                .sorted(Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(location)))
                .limit(limit)
                .toArray(Entity[]::new);
    }

    @Override
    public boolean isSingle() {
        return numberExpr == null;
    }

    @Override
    public @NotNull Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "the nearest " + (numberExpr == null ? "" : numberExpr.toString(e, debug) + " ") + entityDataExpr.toString(e, debug) + " to " + locationExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        numberExpr = (Expression<Number>) exprs[0];
        if (numberExpr instanceof Literal<Number> numberLiteral) {
            int number = numberLiteral.getSingle().intValue();
            if (number == 1)
                numberExpr = null;
            else if (number < 1) {
                Skript.error("you cannot get the nearest " + number + " entities");
                return false;
            }
        }
        entityDataExpr = (Expression<EntityData<?>>) exprs[1];
        locationExpr = (Expression<Location>) exprs[2];
        return true;
    }

}
