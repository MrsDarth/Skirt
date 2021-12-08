package io.github.mrsdarth.skirt.elements.direction;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Toward Away Direction")
@Description("returns a direction towards or away from an entity/location")
@Examples("push all entities away from player")
@Since("2.0.0")

public class ExprTowardAway extends SimpleExpression<Direction> {

    static {
        Skript.registerExpression(ExprTowardAway.class, Direction.class, ExpressionType.COMBINED,
                "[%-number% [(block|met(er|re))[s]]] (toward[s]|1¦away [from]) %entity/location%");
    }

    private Expression<Number> numberExpr;
    private Expression<?> targetExpr;

    private boolean toward;

    @Override
    protected @Nullable
    Direction[] get(@NotNull Event e) {
        Number length = numberExpr == null ? null : numberExpr.getSingle(e);
        return CollectionUtils.array(SkirtDirection.newDirection(targetExpr.getSingle(e), toward, length == null ? null : length.doubleValue()));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Direction> getReturnType() {
        return SkirtDirection.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (numberExpr == null ? "" : numberExpr.toString(e, debug) + " ") + (toward ? "towards " : "away from ") + targetExpr.toString(e, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        numberExpr = (Expression<Number>) exprs[0];
        targetExpr = exprs[1];
        toward = parseResult.mark == 0;
        return true;
    }
}
