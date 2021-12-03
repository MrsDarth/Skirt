package io.github.mrsdarth.skirt.protocolLib.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import io.github.mrsdarth.skirt.protocolLib.PLib;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffAnimation extends Effect {

    static {
        Skript.registerEffect(EffAnimation.class,
                "play (4¦riptide|(3¦off|1¦main)[ ]hand interact) animation on %livingentities%[ for %-players%]",
                "stop entity animation of %livingentities%[ for %-players%]");
    }

    private Expression<LivingEntity> livingEntityExpr;
    private Expression<Player> playerExpr;

    private int flags;

    @Override
    protected void execute(@NotNull Event e) {

        LivingEntity[] livingEntities = livingEntityExpr.getArray(e);

        int index = Skript.isRunningMinecraft(1, 17) ? 8 : 7;
        WrappedDataWatcher.WrappedDataWatcherObject watcherObject = new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(Byte.class));

        if (playerExpr == null) {
            for (LivingEntity livingEntity : livingEntities) {
                WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(livingEntity);
                dataWatcher.setObject(watcherObject, (byte) (dataWatcher.getByte(index) | flags), true);
            }
        } else {
            Player[] players = playerExpr.getArray(e);
            if (players.length == 0) return;
            for (LivingEntity livingEntity : livingEntities) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                packet.getIntegers().write(0, livingEntity.getEntityId());
                List<WrappedWatchableObject> metadata = new ArrayList<>(1);
                metadata.add(new WrappedWatchableObject(watcherObject, (byte) (WrappedDataWatcher.getEntityWatcher(livingEntity).getByte(index) | flags)));
                packet.getWatchableCollectionModifier().write(0, metadata);
                PLib.sendPacket(packet, players);
            }
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        String prefix = livingEntityExpr.toString(e, debug) + (playerExpr == null ? "" : " for " + playerExpr.toString(e, debug));
        return flags == 0 ? "stop entity animation of " + prefix : "play " + switch (flags) {
            case 1 -> "mainhand";
            case 3 -> "offhand";
            case 4 -> "riptide";
            default -> throw new IllegalStateException();
        } + " animation on " + prefix;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        flags = parseResult.mark;
        livingEntityExpr = (Expression<LivingEntity>) exprs[0];
        playerExpr = (Expression<Player>) exprs[1];
        return true;
    }
}
