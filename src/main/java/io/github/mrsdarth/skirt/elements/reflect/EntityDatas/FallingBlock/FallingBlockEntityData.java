package io.github.mrsdarth.skirt.elements.reflect.EntityDatas.FallingBlock;

import ch.njol.skript.entity.FallingBlockData;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTTileEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.Nullable;

public class FallingBlockEntityData extends FallingBlockData {

    private final BlockData blockData;
    private NBTCompound tileEntityData;

    public FallingBlockEntityData(BlockData blockData) {
        this.blockData = blockData;
    }

    public FallingBlockEntityData(Block block) {
        this(block.getBlockData());
        tileEntityData = getTileEntityData(block);
    }

    public static NBTCompound getTileEntityData(Block b) {
        return new NBTTileEntity(b.getState());
    }

    public static void setTileEntityData(FallingBlock fallingBlock, NBTCompound tileEntityData) {
        new NBTEntity(fallingBlock).getOrCreateCompound("TileEntityData").mergeCompound(tileEntityData);
    }

    @Nullable
    @Override
    public FallingBlock spawn(Location loc) {
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc,blockData);
        if (tileEntityData != null) setTileEntityData(fallingBlock, tileEntityData);
        return fallingBlock;
    }

}
