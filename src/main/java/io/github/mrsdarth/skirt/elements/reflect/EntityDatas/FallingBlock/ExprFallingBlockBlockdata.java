package io.github.mrsdarth.skirt.elements.reflect.EntityDatas.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Falling Block Data")
@Description("gets the block data or itemtype of a falling block")
@Examples("set event-block to falling block data of last spawned falling block")
@Since("1.2.2")

public class ExprFallingBlockBlockdata extends SimplePropertyExpression<Entity, Object> {

    static {
        register(ExprFallingBlockBlockdata.class, Object.class, "falling block[ ](1¦data|2¦type)", "entities");
    }

    private boolean isType;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        isType = parseResult.mark == 2;
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "falling block" + (isType ? " type" : "data");
    }

    @Nullable
    @Override
    public Object convert(Entity e) {
        return e instanceof FallingBlock fallingBlock ? isType ? new ItemType(fallingBlock.getBlockData().getMaterial()) : fallingBlock.getBlockData() : null;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return isType ? ItemType.class : BlockData.class;
    }
}
