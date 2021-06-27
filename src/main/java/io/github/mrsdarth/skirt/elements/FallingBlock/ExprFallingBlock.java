package io.github.mrsdarth.skirt.elements.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

@Name("Falling Block")
@Description("returns a falling block entity type with the specified block")
@Examples("spawn falling block of target block")
@Since("1.2.2")

public class ExprFallingBlock extends SimplePropertyExpression<Object, EntityData> {

    static {
        register(ExprFallingBlock.class, EntityData.class, "falling block", "blockdata/blocks/itemtypes");
    }

    @Override
    protected String getPropertyName() {
        return "falling block";
    }

    @Nullable
    @Override
    public EntityData convert(Object o) {
        if (o instanceof BlockData) return new FBData((BlockData) o);
        if (o instanceof Block) return new FBData(((Block) o).getBlockData());
        if (o instanceof ItemType) {
            Material block = ((ItemType) o).getMaterial();
            if (block.isBlock()) return new FBData(block.createBlockData());
        }
        return new FBData();
    }

    @Override
    public Class<? extends EntityData> getReturnType() {
        return EntityData.class;
    }
}
