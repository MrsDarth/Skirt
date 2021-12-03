package io.github.mrsdarth.skirt.elements.direction;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Name("Exact target block")
@Description("returns the exact target block of a living entity, accounting for partial blocks such as stairs and doors")
@Examples("set {_b} to exact target block")
@Since("1.0.0")

public class ExprExactTargetBlock extends PropertyExpression<LivingEntity, Block> {

    static {
        Skript.registerExpression(ExprExactTargetBlock.class, Block.class, ExpressionType.COMBINED,
                "[the] exact target[ed] block [of %livingentities%] [with max %-number% [meters]]");
    }

    private Expression<Number> numberExpr;

    @Override
    protected Block[] get(@NotNull Event e, LivingEntity @NotNull [] source) {
        Number maxDistance = numberExpr == null ? SkriptConfig.maxTargetBlockDistance.value() : numberExpr.getSingle(e);
        return maxDistance == null ? null : get(source, livingEntity -> livingEntity.getTargetBlockExact(maxDistance.intValue()));
    }

    @Override
    public @NotNull Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "the exact target block of " + getExpr().toString(e, debug) + (numberExpr == null ? "" : " with max " + numberExpr.toString(e, debug));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr((Expression<? extends LivingEntity>) exprs[0]);
        numberExpr = (Expression<Number>) exprs[1];
        return true;
    }
}