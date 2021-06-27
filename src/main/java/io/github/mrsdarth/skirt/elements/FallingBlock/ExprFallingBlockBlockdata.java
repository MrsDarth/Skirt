package io.github.mrsdarth.skirt.elements.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.Nullable;

@Name("Falling Block Data")
@Description("gets the block data or itemtype of a falling block")
@Examples("set event-block to falling blockdata of last spawned falling block")
@Since("1.2.2")

public class ExprFallingBlockBlockdata extends SimplePropertyExpression<Entity, Object> {

    static {
        register(ExprFallingBlockBlockdata.class, Object.class, "falling block[ ](1¦data|2¦type)", "entities");
    }

    private boolean isType;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        super.init(exprs, matchedPattern, isDelayed, parseResult);
        isType = parseResult.mark == 2;
        return true;
    }

    @Override
    protected String getPropertyName() {
        return "falling block" + (isType ? "type" : "data");
    }

    @Nullable
    @Override
    public Object convert(Entity e) {
        if (!(e instanceof FallingBlock)) return null;
        BlockData blockData = ((FallingBlock) e).getBlockData();
        return isType ? new ItemType(blockData.getMaterial()) : blockData;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return isType ? ItemType.class : BlockData.class;
    }

}
