package io.github.mrsdarth.skirt.elements.reflect.EntityDatas.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.entity.FallingBlockData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import io.github.mrsdarth.skirt.Skirtness;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Falling Block")
@Description("returns a falling block entity type with the specified block. Works for tile entities as well")
@Examples("spawn falling block of target block")
@Since("1.2.2")

public class ExprFallingBlock extends SimplePropertyExpression<Object, EntityData> {

    static {
        if (Skirtness.hasNBT())
            register(ExprFallingBlock.class, EntityData.class, "falling block", "blocks/blockdatas/itemtypes");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "falling block";
    }

    @Nullable
    @Override
    public EntityData<?> convert(Object o) {
        if (o instanceof Block block) return new FallingBlockEntityData(block);
        if (o instanceof BlockData blockData) return new FallingBlockEntityData(blockData);
        if (o instanceof ItemType itemType) return new FallingBlockEntityData(itemType.getMaterial().createBlockData());
        return null;
    }

    @Override
    public @NotNull Class<? extends EntityData<?>> getReturnType() {
        return FallingBlockData.class;
    }
}
