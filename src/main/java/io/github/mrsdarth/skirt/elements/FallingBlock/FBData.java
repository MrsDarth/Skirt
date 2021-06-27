package io.github.mrsdarth.skirt.elements.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.entity.FallingBlockData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class FBData extends EntityData<FallingBlock> {

    static {
        EntityData.register(FBData.class, "fbdata", FallingBlock.class, "fbdata");
    }

    private BlockData blockData;

    public FBData() {}

    public FBData(BlockData blockData) {
        this.blockData = blockData;
    }


    @Override
    protected boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }


    @Override
    protected boolean init(@Nullable Class<? extends FallingBlock> aClass, @Nullable FallingBlock fallingBlock) {
        return true;
    }

    @Override
    public void set(FallingBlock fallingBlock) {

    }

    @Override
    protected boolean match(FallingBlock fallingBlock) {
        return fallingBlock.getBlockData().matches(blockData);
    }

    @Override
    public Class<? extends FallingBlock> getType() {
        return FallingBlock.class;
    }

    @Override
    public EntityData getSuperType() {
        return new FBData(blockData);
    }

    @Override
    protected int hashCode_i() {
        return Arrays.hashCode(CollectionUtils.array(blockData));
    }

    @Override
    protected boolean equals_i(EntityData<?> e) {
        return e instanceof FBData && ((FBData) e).blockData.matches(blockData);
    }

    @Override
    public boolean isSupertypeOf(EntityData<?> entityData) {
        return equals_i(entityData);
    }

    @Nullable
    @Override
    public FallingBlock spawn(Location loc) {
        return loc.getWorld().spawnFallingBlock(loc,blockData);
    }

    @Override
    public String toString(int flags) {
        return new FallingBlockData(CollectionUtils.array(new ItemType(blockData.getMaterial()))).toString(flags);
    }
}
