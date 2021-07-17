package io.github.mrsdarth.skirt.elements.OtherOther.EntityDatas.FallingBlock;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import io.github.mrsdarth.skirt.Reflectness;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class FBData extends EntityData<FallingBlock> {

    static {
        EntityData.register(FBData.class, "fbdata", FallingBlock.class, "fbdata");
    }

    private BlockData blockData;
    private Object tileEntityData;

    public FBData() {}

    public FBData(BlockData blockData) {
        this.blockData = blockData;
    }

    public FBData(Block block) {
        this.blockData = block.getBlockData();
        tileEntityData = tileEntityData(block);
    }

    private static Object tileEntityData(Block b) {
        try {
            Chunk chunk = b.getChunk();
            Class<?>
                    craftblockClass = Reflectness.craftclass("block.CraftBlock"),
                    nmschunkClass = Reflectness.nmsclass("Chunk"),
                    nbtcompound = Reflectness.nmsclass("NBTTagCompound");
            Object
                    nmsChunk = Reflectness.getHandle(chunk),
                    blockpos = craftblockClass.getDeclaredMethod("getPosition").invoke(craftblockClass.cast(b)),
                    tileEntity = nmschunkClass.getDeclaredMethod("getTileEntityImmediately", blockpos.getClass()).invoke(nmsChunk,blockpos),
                    compound = tileEntity.getClass().getDeclaredMethod("save", nbtcompound).invoke(tileEntity, nbtcompound.getConstructor().newInstance());
            Method deleteTag = nbtcompound.getDeclaredMethod("remove", String.class);
            deleteTag.invoke(compound, "id");
            deleteTag.invoke(compound, "x");
            deleteTag.invoke(compound, "y");
            deleteTag.invoke(compound, "z");
            return compound;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected boolean init(@Nullable Class<? extends FallingBlock> aClass, @Nullable FallingBlock fallingBlock) {
        if (fallingBlock != null) this.blockData = fallingBlock.getBlockData();
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
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc,blockData);
        if (tileEntityData != null) {
            try {
                Class<?>
                        nmsFBClass = Reflectness.nmsclass("Entity"),
                        nbtcompound = Reflectness.nmsclass("NBTTagCompound");
                Constructor<?> compound = nbtcompound.getConstructor();
                Object
                        nmsFB = Reflectness.getHandle(fallingBlock),
                        nbt = nmsFBClass.getDeclaredMethod("save",nbtcompound).invoke(nmsFB, compound.newInstance());
                nbtcompound.getDeclaredMethod("set", String.class, Reflectness.nmsclass("NBTBase")).invoke(nbt, "TileEntityData", tileEntityData);
                nmsFBClass.getDeclaredMethod("load",nbtcompound).invoke(nmsFB, nbt);
            } catch (Exception ex) {ex.printStackTrace();}
        }
        return fallingBlock;
    }

    @Override
    public String toString(int flags) {
        Material type = blockData.getMaterial();
        return "falling " + (type.createBlockData().matches(blockData) ? new ItemType(type) : ("block of " + blockData.getAsString(true)));
    }
}
