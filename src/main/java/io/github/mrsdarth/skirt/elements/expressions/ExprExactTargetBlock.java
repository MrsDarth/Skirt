package io.github.mrsdarth.skirt.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.block.Block;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;


public class ExprExactTargetBlock extends SimpleExpression<Block> {

    static {
        Skript.registerExpression(ExprExactTargetBlock.class, Block.class, ExpressionType.COMBINED,
                "[the] exact target[[t]ed] block[ of %livingentities%][ with max %-number% [metres]]");
    }

    private Expression<LivingEntity> entities;
    private Expression<Number> d;

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    public boolean isSingle() {
        return entities.isSingle();
    }

    @SuppressWarnings({"unchecked", "null"})
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
        entities = (Expression<LivingEntity>) exprs[0];
        d = (Expression<Number>) exprs[1];

        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "exact target block";
    }

    @Override
    @Nullable
    protected Block[] get(Event event) {
        LivingEntity[] l = entities.getArray(event);
        Number d1 = (d != null) ? d.getSingle(event) : 100;
        if (d1 == null) {
            return null;
        }
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (LivingEntity p : l) {
            if (p != null) {
                blocks.add(p.getTargetBlockExact(d1.intValue()));
            }
        }
        return blocks.toArray(new Block[blocks.size()]);
    }
}